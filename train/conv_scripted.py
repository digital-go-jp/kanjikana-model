# Copyright (c) 2024 デジタル庁
# 
# This software is released under the MIT License.
# https://opensource.org/licenses/MIT



# convert pytorch model to jit scripted


import argparse
import torch
import json
import operator
import itertools
from transformer_model import KanjiKanaTransformer, KanjiKanaDataSet, EOS_IDX, BOS_IDX, SPECIAL_SYMBOLS


class KanjiKanaTransformerScripted(KanjiKanaTransformer):
    def __init__(self, args):
        super().__init__(args)

    def convert(self):
        best_checkpoint = torch.load(self.args.model_file, map_location=torch.device(self.args.device))
        params=best_checkpoint['params']

        transformer, optimizer, loss_fn = self.load_by_vocab(best_checkpoint['src_vocab'],best_checkpoint['tgt_vocab'],params)

        with open(self.args.params,'w',encoding='utf-8') as f:
            json.dump(params,f,indent=2,ensure_ascii=False)

        transformer.load_state_dict(best_checkpoint['model_state_dict']) # Seq2SeqTransformer
        optimizer.load_state_dict(best_checkpoint['optimizer_state_dict'])

        scripted = torch.jit.script(transformer)
        scripted.save(self.args.model_script)

        encoder = torch.jit.script(transformer.transformer.encoder)
        encoder.save(self.args.encoder)
        decoder = torch.jit.script(transformer.transformer.decoder)
        decoder.save(self.args.decoder)
        torch.jit.script(transformer.generator).save(self.args.generator)
        torch.jit.script(transformer.positional_encoding).save(self.args.positional_encoding)
        torch.jit.script(transformer.src_tok_emb).save(self.args.src_tok_emb)
        torch.jit.script(transformer.tgt_tok_emb).save(self.args.tgt_tok_emb)

        with open(self.args.vocab_src,'w',encoding='utf-8') as f:
            for v in self.vocab_transform[self.args.source_lang].get_itos():
                f.write(f"{v}\n")

        with open(self.args.vocab_tgt,'w',encoding='utf-8') as f:
            for v in self.vocab_transform[self.args.target_lang].get_itos():
                f.write(f"{v}\n")


def main():
    import os
    with open('../version.txt') as f:
        ver=f.readline().rstrip()
    print(f'ver={ver}')
    DIR=f'model/model.{ver}'
    # 引数の処理
    parser = argparse.ArgumentParser(description='')
    parser.add_argument('--model_file', default=f'{DIR}/checkpoint_best.pt', type=str)
    parser.add_argument('--model_script', default=f"{DIR}/script_{ver}.pt", type=str)
    parser.add_argument('--encoder', default=f"{DIR}/encoder_{ver}.pt", type=str)
    parser.add_argument('--decoder', default=f"{DIR}/decoder_{ver}.pt", type=str)
    parser.add_argument('--positional_encoding', default=f"{DIR}/positional_encoding_{ver}.pt", type=str)
    parser.add_argument('--generator', default=f"{DIR}/generator_{ver}.pt", type=str)
    parser.add_argument('--src_tok_emb', default=f"{DIR}/src_tok_emb_{ver}.pt", type=str)
    parser.add_argument('--tgt_tok_emb', default=f"{DIR}/tgt_tok_emb_{ver}.pt", type=str)
    parser.add_argument('--vocab_src', default=f"{DIR}/vocab_src_{ver}.txt", type=str)
    parser.add_argument('--vocab_tgt', default=f"{DIR}/vocab_tgt_{ver}.txt", type=str)
    parser.add_argument('--params',default=f"{DIR}/params_{ver}.json", type=str)
    parser.add_argument('--device',default='cpu',choices=('cuda','cpu','mps'))


    args = parser.parse_args()

    KanjiKanaTransformerScripted(args).convert()



if __name__ == '__main__':
    main()
