#!/bin/env python
# coding:utf-8

# Copyright (c) 2025 デジタル庁
#
# This software is released under the MIT License.
# https://opensource.org/licenses/MIT

"""
学習済みのモデルを用いて，漢字からカナ，カナから漢字の推論を行う。推論の際には，GreedyサーチとBeamサーチを選択して実行する
"""

import sys

sys.path.append("../")
import argparse
import torch
from torch.utils.data import Dataset
from typing import List, Tuple

from char_model import KanjiKanaTransformer, EOS_IDX, BOS_IDX, SPECIAL_SYMBOLS, char_tokenizer


# https://qiita.com/Shoelife2022/items/7f2b5e916ebd68ca2c23
# https://github.com/budzianowski/PyTorch-Beam-Search-Decoding/blob/master/decode_beam.py

class BeamSearchNode(object):
    def __init__(self, prevNode, decoder_input, logProb, length):
        self.prevNode = prevNode
        self.decoder_input = decoder_input
        self.logProb = logProb
        self.length = length

    def eval(self, alpha=0.6):
        # https://qiita.com/gacky01/items/87c435e89c75e5ff2464
        return self.logProb / (((6 + self.length) / (5 + 1)) ** alpha)


class KanjiKanaData(Dataset):
    def __init__(self, src_sentence, tgt_sentence, transforms_src, transforms_tgt) -> None:
        super().__init__()
        self.transforms_src = transforms_src
        self.transforms_tgt = transforms_tgt
        self.src_sentence = src_sentence
        self.tgt_sentence = tgt_sentence

    # ここで取り出すデータを指定している
    def __getitem__(
            self,
            index: int
    ) -> Tuple[str, str]:
        # データの変形 (transforms)
        src_batch = self.transforms_src(self.src_sentence)
        tgt_batch = self.transforms_tgt(self.tgt_sentence)

        return " ".join(src_batch), " ".join(tgt_batch)

    # この method がないと DataLoader を呼び出す際にエラーを吐かれる
    def __len__(self) -> int:
        return len(self.data)


class KanjiKanaTransformerTest(KanjiKanaTransformer):
    def __init__(self, args):
        super().__init__(args)

    def greedy_decode(self, model, src, src_mask, max_len, start_symbol):
        # src[length,batchsize]
        # src_mask[length,length]

        src = src.to(self.args.device)
        src_mask = src_mask.to(self.args.device)
        with torch.no_grad():
            memory = model.encode(src, src_mask)  # memory[length,batchsize,hiddensize]
        ys = torch.ones(1, 1).fill_(start_symbol).type(torch.long).to(self.args.device)  # ys[nowlength,batchsize]
        sum_prob = 0
        for i in range(max_len - 1):
            memory = memory.to(self.args.device)
            tgt_mask = (self.generate_square_subsequent_mask(ys.size(0))
                        .type(torch.bool)).to(self.args.device)  # tgt_mask[length,batchsize]
            with torch.no_grad():
                out = model.decode(ys, memory, tgt_mask)  # out[length,batchsize,hiddensize]
                out = out.transpose(0, 1)  #
                prob = model.generator(out[:, -1])  # prob[ ,tgtwordkind]
                prob = torch.nn.functional.softmax(prob, dim=1)
                next_prob, next_word = torch.max(prob, dim=1)
                next_word = next_word.item()
                sum_prob += torch.log(next_prob)

            ys = torch.cat([ys, torch.ones(1, 1).type_as(src.data).fill_(next_word)], dim=0)  # ys(2,1) ys[[2].[9]]
            if next_word == EOS_IDX:
                break
        return ys.flatten(), sum_prob.item()  # ys [length,batch]

    def beam_decode(self, model, src, src_mask, max_len, beam_width, n_best, start_symbol=BOS_IDX):

        assert n_best <= beam_width
        src = src.to(self.args.device)
        src_mask = src_mask.to(self.args.device)
        with torch.no_grad():
            memory = model.encode(src, src_mask)  # memory[length,batchsize,hiddensize]
        ys = torch.ones(1, 1).fill_(start_symbol).type(torch.long).to(
            self.args.device)  # ys[nowlength,batchsize] wordindex as integer

        # starting node
        node = BeamSearchNode(prevNode=None, decoder_input=ys, logProb=0, length=0)
        best_nodes = [(-node.eval(), node)]

        def concat_input(n: BeamSearchNode):
            y = []
            while True:
                if n == None:
                    break
                y.append(n.decoder_input.item())
                n = n.prevNode
            y.reverse()
            return torch.tensor(y, dtype=torch.long).unsqueeze(1)

        for i in range(max_len - 1):
            memory = memory.to(self.args.device)
            updated = False
            cand_nodes = []
            for _, n in best_nodes:
                if n.decoder_input.item() == EOS_IDX:
                    cand_nodes.append((-n.eval(), n))
                else:
                    updated = True
                    with torch.no_grad():
                        ys = concat_input(n)
                        ys = ys.to(self.args.device)
                        tgt_mask = (self.generate_square_subsequent_mask(ys.size(0)).type(torch.bool)).to(
                            self.args.device)  # tgt_mask[length,batchsize]

                        out = model.decode(ys, memory, tgt_mask)  # out[length,batchsize,hiddensize]
                        out = out.transpose(0, 1)
                        prob = model.generator(out[:, -1])  # prob[ ,tgtwordkind]
                        prob = torch.nn.functional.softmax(prob, dim=1)
                        next_probs, next_words = torch.topk(prob, beam_width, dim=1)

                        for p, w in zip(next_probs.permute(1, 0), next_words.permute(1, 0)):
                            new_node = BeamSearchNode(prevNode=n, decoder_input=torch.unsqueeze(w, 0),
                                                      logProb=n.logProb + torch.log(p), length=n.length + 1)
                            cand_nodes.append((-new_node.eval(), new_node))

            sorted_cand_nodes = sorted(cand_nodes, key=lambda x: x[0], reverse=False)
            best_nodes = sorted_cand_nodes[:n_best]
            if not updated:
                break

        tokens = []  #
        probs = []
        for score, n in best_nodes:
            token = []
            probs.append(n.logProb)
            while True:
                token.append(n.decoder_input.item())
                n = n.prevNode
                if n == None:
                    break
            token.reverse()
            tokens.append(torch.Tensor(token))
        return tokens, probs

    # スペーススペーススペースはスペースにするため別メソッドとした   replace(" ","")では全部消えてしまう
    def remove_space(self, s):
        lst = []
        i = 0
        while i < len(s):
            if s[i] == " ":
                if i + 1 < len(s):
                    lst.append(s[i + 1])
                    i += 1
            else:
                lst.append(s[i])
            i += 1
        return "".join(lst)

    def generate(self, src_sentence, tgt_sentence):
        last_checkpoint = torch.load(self.args.model_file, map_location=torch.device(self.args.device))

        transformer, optimizer, loss_fn = self.load_by_vocab(last_checkpoint['src_vocab'], last_checkpoint['tgt_vocab'],
                                                             last_checkpoint["params"])

        transformer.load_state_dict(last_checkpoint['model_state_dict'])
        optimizer.load_state_dict(last_checkpoint['optimizer_state_dict'])
        num_epochs = last_checkpoint['epoch']

        params = last_checkpoint["params"]

        transformer.eval()

        iter = KanjiKanaData(src_sentence, tgt_sentence, char_tokenizer, char_tokenizer)

        src_sent,tgt_sent=iter.__getitem__(0)

        src = self.text_transform[self.args.source_lang](src_sent).view(-1, 1)
        num_tokens = src.shape[0]
        src_mask = (torch.zeros(num_tokens, num_tokens)).type(torch.bool)

        res = []

        if self.args.search == 'greedy':

            pred_token, pred_prob = self.greedy_decode(transformer, src, src_mask, max_len=self.args.max_len,
                                                       start_symbol=BOS_IDX)
            predict_sentence = "".join(
                self.vocab_transform[self.args.target_lang].lookup_tokens(list(pred_token.cpu().numpy()))).replace(
                SPECIAL_SYMBOLS[BOS_IDX], "").replace(SPECIAL_SYMBOLS[EOS_IDX], "")
            target_sentence = self.remove_space(tgt_sentence)
            src_sentence = self.remove_space(src_sentence)

            res.append(Result('greedy', src_sentence, target_sentence, predict_sentence, pred_prob))


        elif self.args.search == 'beam':

            pred_tokens, pred_probs = self.beam_decode(transformer, src, src_mask, self.args.max_len,
                                                       self.args.beam_width, self.args.nbest, start_symbol=BOS_IDX)
            for i, (pred_token, pred_prob) in enumerate(zip(pred_tokens, pred_probs)):
                predict_sentence = "".join(
                    self.vocab_transform[self.args.target_lang].lookup_tokens(list(pred_token.cpu().numpy()))).replace(
                    SPECIAL_SYMBOLS[BOS_IDX], "").replace(SPECIAL_SYMBOLS[EOS_IDX], "")
                target_sentence = self.remove_space(tgt_sentence)
                src_sentence = self.remove_space(src_sentence)
                res.append(Result('greedy', src_sentence, target_sentence, predict_sentence, pred_prob))

        return res


class Result:
    def __init__(self, search_type, src_sentence, tgt_sentence, pred_sentence, pred_prob):
        self.search_type = search_type
        self.src_sentence = src_sentence
        self.tgt_sentece = tgt_sentence
        self.pred_sentence = pred_sentence
        self.pred_prob = pred_prob

    def __repr__(self):
        return f"Result(search_type={self.search_type},src_sentence={self.src_sentence},tgt_sentence={self.tgt_sentece},pred_sentence={self.pred_sentence},pred_prob={self.pred_prob})"



class Args:
    def __init__(self):
        self.model_file='model/checkpoint_best.pt'
        self.device='cpu'
        self.nbest=5
        self.beam_width=5
        self.max_len=100
        self.prefix='translation'
        self.source_lang='kanji'
        self.target_lang='kana'
        self.search='greedy'


if __name__ == '__main__':
    args = Args()
    kkt = KanjiKanaTransformerTest(args)
    print(kkt.generate('田中五郎', 'タカカゴロウ'))
