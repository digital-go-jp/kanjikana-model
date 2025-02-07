#!/bin/env python
# coding:utf-8

# Copyright (c) 2025 デジタル庁
#
# This software is released under the MIT License.
# https://opensource.org/licenses/MIT

"""
漢字からカナ，もしくは，カナから漢字のデータを入力として与えて，Transformerモデルを作成する，漢字とカナは文字単位で学習する
"""

import warnings
warnings.simplefilter('ignore')

from typing import Iterable, List

from torch import Tensor
import torch
import torch.nn as nn
from torch.nn import Transformer
import math
from torch.nn.utils.rnn import pad_sequence
from timeit import default_timer as timer
from torch.utils.data import DataLoader
from torch.utils.tensorboard import SummaryWriter
import argparse
import json
import os
import glob
from torch.utils.data import Dataset
from typing import List, Tuple

import random
import numpy as np

seed=0
torch.manual_seed(seed)
random.seed(seed)
np.random.seed(seed)
torch.manual_seed(seed)
torch.backends.cudnn.benchmark = False
torch.backends.cudnn.deterministic = True

def seed_worker(worker_id):
    worker_seed = torch.initial_seed() % 2**32
    np.random.seed(worker_seed)
    random.seed(worker_seed)

my_generator = torch.Generator()
my_generator.manual_seed(seed)



# Define special symbols and indices
UNK_IDX, PAD_IDX, BOS_IDX, EOS_IDX = 0, 1, 2, 3
# Make sure the tokens are in order of their indices to properly insert them in vocab
SPECIAL_SYMBOLS = ['<unk>', '<pad>', '<bos>', '<eos>']

def split_tokenizer(x):  # noqa: F821
    # type: (str) -> List[str]
    return  [t if len(t)>0 else " " for t in x.replace("  "," ").split(" ")]  # 空白も返す

def char_tokenizer(string):
    lst=[]
    for i in range(len(string)):
        lst.append(string[i])
    return lst

class Vocab:

    def __init__(self):
        self.itos=[]
        self.stoi={}

    def set_by_token(self, tokenizer,tokens ):
        self.itos=[]
        self.stoi={}

        def set(words):
            for t in words:
                if t not in self.stoi:
                    n = len(self.stoi)
                    self.stoi[t] = n
                    self.itos.append(t)

        set(SPECIAL_SYMBOLS)
        for token in tokens:
            set(tokenizer(token))

    def set_by_vocab(self, itos):
        self.itos = itos

        stoi={}
        for i,v in enumerate(itos):
            stoi[v]=i
        self.stoi=stoi


    def __len__(self):
        return len(self.stoi)

    def __call__(self, token:List[str]):
        unk_token=SPECIAL_SYMBOLS[UNK_IDX]
        return [self.stoi[t] if t in self.stoi else self.stoi[unk_token] for t in token ]

    def lookup_tokens(self, tokens:List[int]):
        return [self.itos[int(i)] for i in tokens]

# helper Module that adds positional encoding to the token embedding to introduce a notion of word order.
class PositionalEncoding(nn.Module):
    def __init__(self,
                 emb_size: int,
                 dropout: float,
                 maxlen: int = 5000):
        super(PositionalEncoding, self).__init__()
        den = torch.exp(- torch.arange(0, emb_size, 2)* math.log(10000) / emb_size)
        pos = torch.arange(0, maxlen).reshape(maxlen, 1)
        pos_embedding = torch.zeros((maxlen, emb_size))
        pos_embedding[:, 0::2] = torch.sin(pos * den)
        pos_embedding[:, 1::2] = torch.cos(pos * den)
        pos_embedding = pos_embedding.unsqueeze(-2)

        self.dropout = nn.Dropout(dropout)
        self.register_buffer('pos_embedding', pos_embedding)

    def forward(self, token_embedding: Tensor):
        return self.dropout(token_embedding + self.pos_embedding[:token_embedding.size(0), :])

# helper Module to convert tensor of input indices into corresponding tensor of token embeddings
class TokenEmbedding(nn.Module):
    def __init__(self, vocab_size: int, emb_size, device):
        super(TokenEmbedding, self).__init__()
        self.embedding = nn.Embedding(vocab_size, emb_size, device=device)
        self.emb_size = emb_size

    def forward(self, tokens: Tensor):
        return self.embedding(tokens.long()) * math.sqrt(self.emb_size)

# Seq2Seq Network
class Seq2SeqTransformer(nn.Module):
    def __init__(self,
                 num_encoder_layers: int,
                 num_decoder_layers: int,
                 emb_size: int,
                 nhead: int,
                 src_vocab_size: int,
                 tgt_vocab_size: int,
                 dim_feedforward: int,
                 dropout: float,
                 device):
        super(Seq2SeqTransformer, self).__init__()
        self.transformer = Transformer(d_model=emb_size,
                                       nhead=nhead,
                                       num_encoder_layers=num_encoder_layers,
                                       num_decoder_layers=num_decoder_layers,
                                       dim_feedforward=dim_feedforward,
                                       dropout=dropout,device=device)
        self.generator = nn.Linear(emb_size, tgt_vocab_size)
        self.src_tok_emb = TokenEmbedding(src_vocab_size, emb_size, device)
        self.tgt_tok_emb = TokenEmbedding(tgt_vocab_size, emb_size, device)
        self.positional_encoding = PositionalEncoding(
            emb_size, dropout=dropout)

    def forward(self,
                src: Tensor,
                trg: Tensor,
                src_mask: Tensor,
                tgt_mask: Tensor,
                src_padding_mask: Tensor,
                tgt_padding_mask: Tensor,
                memory_key_padding_mask: Tensor):
        src_emb = self.positional_encoding(self.src_tok_emb(src))
        tgt_emb = self.positional_encoding(self.tgt_tok_emb(trg))
        outs = self.transformer(src_emb, tgt_emb, src_mask, tgt_mask, None,
                                src_padding_mask, tgt_padding_mask, memory_key_padding_mask)
        return self.generator(outs)

    @torch.jit.export
    def encode(self, src: Tensor, src_mask: Tensor):
        return self.transformer.encoder(self.positional_encoding(
                            self.src_tok_emb(src)), src_mask)

    @torch.jit.export
    def decode(self, tgt: Tensor, memory: Tensor, tgt_mask: Tensor):
        return self.transformer.decoder(self.positional_encoding(
                          self.tgt_tok_emb(tgt)), memory,
                          tgt_mask)

#def transforms(string):
#    lst=[]
#    for i in range(len(string)):
#        lst.append(string[i])
#    return lst

class KanjiKanaDataSet(Dataset):
    def __init__(self,args, root, transforms_src, transforms_tgt) -> None:
        super().__init__()
        self.transforms_src = transforms_src
        self.transforms_tgt = transforms_tgt
        self.args =  args
        src=[]
        tgt=[]
        with open(root,'r',encoding='utf-8') as f:
            for l in f:
                jdata = json.loads(l)
                src.append(jdata[self.args.prefix][self.args.source_lang])
                tgt.append(jdata[self.args.prefix][self.args.target_lang])
        self.data=[]
        for s,t in zip(src,tgt):
            self.data.append((s,t))

    # ここで取り出すデータを指定している
    def __getitem__(
        self,
        index: int
    ) -> Tuple[str, str]:

        # データの変形 (transforms)
        src_batch = self.transforms_src(self.data[index][0])
        tgt_batch= self.transforms_tgt(self.data[index][1])

        return " ".join(src_batch), " ".join(tgt_batch)

    # この method がないと DataLoader を呼び出す際にエラーを吐かれる
    def __len__(self) -> int:
        return len(self.data)


class KanjiKanaTransformer:
    def __init__(self, args):
        self.args = args
        self.text_transform = {}
        self.token_transform = {}
        self.vocab_transform = {}

    def generate_square_subsequent_mask(self,sz):
        mask = (torch.triu(torch.ones((sz, sz), device=self.args.device)) == 1).transpose(0, 1)
        mask = mask.float().masked_fill(mask == 0, float('-inf')).masked_fill(mask == 1, float(0.0))
        return mask

    def create_mask(self,src, tgt):
        src_seq_len = src.shape[0]
        tgt_seq_len = tgt.shape[0]

        tgt_mask = self.generate_square_subsequent_mask(tgt_seq_len)
        src_mask = torch.zeros((src_seq_len, src_seq_len),device=self.args.device).type(torch.bool)

        src_padding_mask = (src == PAD_IDX).transpose(0, 1)
        tgt_padding_mask = (tgt == PAD_IDX).transpose(0, 1)
        return src_mask, tgt_mask, src_padding_mask, tgt_padding_mask

    # helper function to club together sequential operations
    def sequential_transforms(self,*transforms):
        def func(txt_input):
            for transform in transforms:
                txt_input = transform(txt_input)
            return txt_input
        return func

    # function to add BOS/EOS and create tensor for input sequence indices
    def tensor_transform(self,token_ids: List[int]):
        return torch.cat((torch.tensor([BOS_IDX]),
                          torch.tensor(token_ids),
                          torch.tensor([EOS_IDX])))

    # function to collate data samples into batch tensors
    # batch [('Ein Typ, der blau trägt, in einem Loch.', 'A guy wearing blue in a hole.'),
    def collate_fn(self,batch):
        src_batch, tgt_batch = [], []
        for src_sample, tgt_sample in batch:
            src_batch.append(self.text_transform[self.args.source_lang](src_sample.rstrip("\n")))
            tgt_batch.append(self.text_transform[self.args.target_lang](tgt_sample.rstrip("\n")))

        src_batch = pad_sequence(src_batch, padding_value=PAD_IDX)
        tgt_batch = pad_sequence(tgt_batch, padding_value=PAD_IDX)
        return src_batch, tgt_batch

    def train_epoch(self, train_iter, model, optimizer, loss_fn):
        model.train()
        losses = 0

        train_dataloader = DataLoader(train_iter, batch_size=self.args.batch_size,  collate_fn=self.collate_fn, shuffle=True, worker_init_fn=seed_worker,generator=my_generator)

        for i, (src, tgt) in enumerate(train_dataloader):
            src = src.to(self.args.device)
            tgt = tgt.to(self.args.device)

            tgt_input = tgt[:-1, :]

            src_mask, tgt_mask, src_padding_mask, tgt_padding_mask = self.create_mask(src, tgt_input)

            logits = model(src, tgt_input, src_mask, tgt_mask,src_padding_mask, tgt_padding_mask, src_padding_mask)

            optimizer.zero_grad()

            tgt_out = tgt[1:, :]
            loss = loss_fn(logits.reshape(-1, logits.shape[-1]), tgt_out.reshape(-1))
            loss.backward()

            optimizer.step()
            losses += loss.item()

        return losses / len(list(train_dataloader))


    def evaluate(self, model, loss_fn):
        model.eval()
        losses = 0

        val_iter = KanjiKanaDataSet(self.args, self.args.valid_file, char_tokenizer, char_tokenizer)
        val_dataloader = DataLoader(val_iter, batch_size=self.args.batch_size, collate_fn=self.collate_fn, shuffle=True,worker_init_fn=seed_worker,generator=my_generator)

        for src, tgt in val_dataloader:
            src = src.to(self.args.device)
            tgt = tgt.to(self.args.device)

            tgt_input = tgt[:-1, :]

            src_mask, tgt_mask, src_padding_mask, tgt_padding_mask = self.create_mask(src, tgt_input)

            logits = model(src, tgt_input, src_mask, tgt_mask,src_padding_mask, tgt_padding_mask, src_padding_mask)

            tgt_out = tgt[1:, :]
            loss = loss_fn(logits.reshape(-1, logits.shape[-1]), tgt_out.reshape(-1))
            losses += loss.item()

        return losses / len(list(val_dataloader))

    def load_by_vocab(self, src_vocab, tgt_vocab, params):

        self.args.emb_size=params["emb_size"]
        self.args.nhead=params["nhead"]

        self.args.ffn_hid_dim=params['ffn_hid_dim']
        self.args.batch_size=params['batch_size']
        self.args.num_encoder_layers=params['num_encoder_layers']
        self.args.num_decoder_layers=params['num_decoder_layers']
        self.args.num_epochs=params['num_epochs']
        self.args.lr=params['lr']
        self.args.dropout=params['dropout']
        self.args.adam_eps=params['adam_eps']
        self.args.prefix=params['prefix']
        #self.args.source_lang=params['source_lang']
        #self.args.target_lang=params['target_lang']

        self.token_transform[self.args.source_lang] = split_tokenizer
        self.token_transform[self.args.target_lang] = split_tokenizer

        svocab = Vocab()
        svocab.set_by_vocab(src_vocab)
        self.vocab_transform[params["source_lang"]] = svocab

        tvocab = Vocab()
        tvocab.set_by_vocab(tgt_vocab)
        self.vocab_transform[params["target_lang"]] = tvocab

        # ``src`` and ``tgt`` language text transforms to convert raw strings into tensors indices
        for ln in [params["source_lang"], params["target_lang"]]:
            self.text_transform[ln] = self.sequential_transforms(self.token_transform[ln],  # Tokenization
                                                       self.vocab_transform[ln],  # Numericalization
                                                       self.tensor_transform)  # Add BOS/EOS and create tensor
        # Set ``UNK_IDX`` as the default index. This index is returned when the token is not found.
        # If not set, it throws ``RuntimeError`` when the queried token is not found in the Vocabulary.
        #for ln in [params["source_lang"], params["target_lang"]]:
        #    self.vocab_transform[ln].set_default_index(UNK_IDX)


        src_vocab_size = len(self.vocab_transform[params["source_lang"]])
        tgt_vocab_size = len(self.vocab_transform[params["target_lang"]])

        transformer = KanjiKanaTransformer.load_model(params["num_encoder_layers"], params["num_decoder_layers"], params["emb_size"],params["nhead"], src_vocab_size, tgt_vocab_size, params["ffn_hid_dim"], params["dropout"],self.args.device)

        transformer = transformer.to(self.args.device)
        loss_fn = KanjiKanaTransformer.load_lossfn()

        optimizer = KanjiKanaTransformer.load_optimizer(transformer, params["lr"], params["adam_eps"])
        return transformer , optimizer, loss_fn


    def load(self, train_iter):

        def yield_tokens(data_iter: Iterable, language: str) -> List[str]:
            language_index = {self.args.source_lang: 0, self.args.target_lang: 1}

            for data_sample in data_iter:
                yield data_sample[language_index[language]]

        self.token_transform[self.args.source_lang] = split_tokenizer
        self.token_transform[self.args.target_lang] = split_tokenizer

        src=[t for t in yield_tokens(train_iter, self.args.source_lang)]
        tgt=[t for t in yield_tokens(train_iter, self.args.target_lang)]

        svocab = Vocab()
        svocab.set_by_token(split_tokenizer, src)
        self.vocab_transform[self.args.source_lang] = svocab

        tvocab = Vocab()
        tvocab.set_by_token(split_tokenizer, tgt)
        self.vocab_transform[self.args.target_lang] = tvocab


        # ``src`` and ``tgt`` language text transforms to convert raw strings into tensors indices
        for ln in [self.args.source_lang, self.args.target_lang]:
            self.text_transform[ln] = self.sequential_transforms(self.token_transform[ln],  # Tokenization
                                                       self.vocab_transform[ln],  # Numericalization
                                                       self.tensor_transform)  # Add BOS/EOS and create tensor
        # Set ``UNK_IDX`` as the default index. This index is returned when the token is not found.
        # If not set, it throws ``RuntimeError`` when the queried token is not found in the Vocabulary.
        #for ln in [self.args.source_lang, self.args.target_lang]:
        #    self.vocab_transform[ln].set_default_index(UNK_IDX)


        src_vocab_size = len(self.vocab_transform[self.args.source_lang])
        tgt_vocab_size = len(self.vocab_transform[self.args.target_lang])


        transformer = KanjiKanaTransformer.load_model(self.args.num_encoder_layers, self.args.num_decoder_layers, self.args.emb_size,self.args.nhead, src_vocab_size, tgt_vocab_size, self.args.ffn_hid_dim, self.args.dropout,self.args.device)

        transformer = transformer.to(self.args.device)
        loss_fn = KanjiKanaTransformer.load_lossfn()

        optimizer = KanjiKanaTransformer.load_optimizer(transformer, self.args.lr, self.args.adam_eps)
        return transformer , optimizer, loss_fn

    @classmethod
    def load_model(cls, num_encoder_layers, num_decoder_layers, emb_size,nhead, src_vocab_size, tgt_vocab_size, ffn_hid_dim, dropout, device):
        transformer = Seq2SeqTransformer(num_encoder_layers, num_decoder_layers,emb_size,
                                         nhead, src_vocab_size, tgt_vocab_size, ffn_hid_dim, dropout, device)

        for p in transformer.parameters():
            if p.dim() > 1:
                nn.init.xavier_uniform_(p)
        return transformer

    @classmethod
    def load_lossfn(cls):
        loss_fn = torch.nn.CrossEntropyLoss(ignore_index=PAD_IDX)
        return loss_fn

    @classmethod
    def load_optimizer(cls, transformer, lr, adam_eps):
        return torch.optim.Adam(transformer.parameters(), lr=lr, betas=(0.9, 0.98), eps=adam_eps)

    def train(self):
        train_iter = KanjiKanaDataSet(self.args, self.args.train_file, char_tokenizer , char_tokenizer)
        transformer, optimizer, loss_fn = self.load(train_iter)


        writer=None
        if len(self.args.tensorboard_logdir)>0:
            writer = SummaryWriter(log_dir=self.args.tensorboard_logdir)

        best_loss=None
        num_epochs = self.args.num_epochs
        now_epoch =0
        best_epoch=0
        now_loss=None

        params = {"emb_size": self.args.emb_size, "nhead": self.args.nhead, "ffn_hid_dim": self.args.ffn_hid_dim,
                  "dropout": self.args.dropout, "lr": self.args.lr, "adam_eps": self.args.adam_eps,
                  "num_encoder_layers": self.args.num_encoder_layers,
                  "num_decoder_layers": self.args.num_decoder_layers, "prefix": self.args.prefix,
                  "source_lang": self.args.source_lang, "target_lang": self.args.target_lang,
                  "batch_size": self.args.batch_size, "num_epochs": num_epochs}

        best_optimizer=None
        best_transformer=None
        # 学習中，済みのものがあればロードし，途中から計算する。
        if os.path.exists(self.args.output_dir):
            files = list(sorted(glob.glob(self.args.output_dir + "/checkpoint_*.pt"), reverse=True))
            if len(files) >= 1:

                best_checkpoint = torch.load(files[0], map_location=torch.device(self.args.device))

                best_transformer, best_optimizer, loss_fn = self.load_by_vocab(best_checkpoint['src_vocab'],
                                                                     best_checkpoint['tgt_vocab'],
                                                                     best_checkpoint["params"])

                best_transformer.load_state_dict(best_checkpoint['model_state_dict'])
                best_optimizer.load_state_dict(best_checkpoint['optimizer_state_dict'])

                best_epoch = best_checkpoint['epoch']
                if 'val_loss' in best_checkpoint:
                    best_loss = best_checkpoint['val_loss']
                print(f"load:{files[0]},best_epoch={best_epoch},best_loss={best_loss}")

            if len(files) >=2:
                try:
                    last_checkpoint = torch.load(files[1], map_location=torch.device(self.args.device))

                    transformer, optimizer, loss_fn = self.load_by_vocab(last_checkpoint['src_vocab'],
                                                                                   last_checkpoint['tgt_vocab'],
                                                                                   last_checkpoint["params"])

                    transformer.load_state_dict(last_checkpoint['model_state_dict'])
                    optimizer.load_state_dict(last_checkpoint['optimizer_state_dict'])

                    now_epoch = last_checkpoint['epoch']
                    if 'val_loss' in last_checkpoint:
                        now_loss = last_checkpoint['val_loss']
                except:
                    pass
                print(f"load:{files[1]},now_epoch={now_epoch},now_loss={now_loss}")

        os.makedirs(self.args.output_dir, exist_ok=True)
        print(f'num_epochs:{num_epochs}')


        if now_epoch<best_epoch:
            now_epoch=best_epoch
            optimizer=best_optimizer
            transformer=best_transformer


        patient=0
        #keta=math.ceil(math.log10(self.args.num_epocs))
        for epoch in range(now_epoch+1, num_epochs+1):
            start_time = timer()
            train_loss = self.train_epoch(train_iter, transformer, optimizer, loss_fn)
            end_time = timer()
            val_loss = self.evaluate(transformer, loss_fn)
            print((f"Epoch: {epoch}, Train loss: {train_loss:.3f}, Val loss: {val_loss:.3f}, "f"Epoch time = {(end_time - start_time):.3f}s"))

            # https://wandb.ai/wandb_fc/japanese/reports/PyTorch---VmlldzoxNTAyODQy
            torch.save({'epoch': epoch, 'model_state_dict':transformer.state_dict(), 'optimizer_state_dict':optimizer.state_dict(),'loss':train_loss, 'val_loss':val_loss, 'src_vocab':self.vocab_transform[self.args.source_lang].itos,"tgt_vocab":self.vocab_transform[self.args.target_lang].itos,"params":params},self.args.output_dir+f"/checkpoint_{epoch:03d}.pt")

            # https://zenn.dev/a5chin/articles/log_tensorboard
            if writer is not None:
                writer.add_scalar('loss',train_loss, epoch)
                writer.add_scalar('val_loss', val_loss, epoch )

            if best_loss is None or best_loss > val_loss:
                best_loss = val_loss
                torch.save({'epoch': epoch, 'model_state_dict': transformer.state_dict(),
                            'optimizer_state_dict': optimizer.state_dict(), 'loss': train_loss, 'val_loss':val_loss, 'src_vocab':self.vocab_transform[self.args.source_lang].itos,"tgt_vocab":self.vocab_transform[self.args.target_lang].itos,"params":params},
                           self.args.output_dir + f"/checkpoint_best.pt")
                patient=0
            else:
                patient+=1
            files = list(sorted(glob.glob(self.args.output_dir + "/checkpoint_*.pt"), reverse=True))
            for fname in files[self.args.save_num+1:]:
                os.remove(fname)

            if patient > self.args.earlystop_patient:
                break


def main():
    # 引数の処理
    parser = argparse.ArgumentParser(description='')
    parser.add_argument('--emb_size', default=512, type=int)
    parser.add_argument('--nhead', default=8, type=int)
    parser.add_argument('--ffn_hid_dim', default=2048, type=int)
    parser.add_argument('--batch_size', default=32, type=int)
    parser.add_argument('--num_encoder_layers', default=8, type=int)
    parser.add_argument('--num_decoder_layers', default=8, type=int)
    parser.add_argument('--num_epochs', default=145, type=int)
    parser.add_argument('--lr', default=0.0002, type=float)
    parser.add_argument('--dropout', default=0.3, type=float)
    parser.add_argument('--adam_eps', default=1e-03, type=float)
    parser.add_argument('--train_file', default='dataset_r.1.6.1o/train.jsonl', type=str)
    parser.add_argument('--valid_file', default='dataset_r.1.6.1o/valid.jsonl', type=str)
    parser.add_argument('--output_dir', default='model_r.1.6.1o', type=str)
    parser.add_argument('--prefix', default='translation', type=str)
    parser.add_argument('--source_lang', default='kana', type=str)
    parser.add_argument('--target_lang', default='kanji', type=str)
    parser.add_argument('--save_num', default=1, type=int)
    parser.add_argument('--device',default='cpu',choices=('cuda','cpu','mps'))
    parser.add_argument('--tensorboard_logdir',default='logs_r.1.6.1o',type=str)
    parser.add_argument('--earlystop_patient',default=99999,type=int,help="number of times not updated from valid best")

    args = parser.parse_args()

    KanjiKanaTransformer(args).train()


if __name__ == '__main__':
    main()
