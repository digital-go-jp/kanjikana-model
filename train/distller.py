
import glob
from timeit import default_timer as timer   
from transformer_model import KanjiKanaTransformer, KanjiKanaDataSet, BOS_IDX, EOS_IDX, PAD_IDX, SPECIAL_SYMBOLS
import torch
import torch.nn.functional as F
from torch.utils.data import DataLoader
import argparse
import os
from torch.utils.tensorboard import SummaryWriter

class KanjiKanaDistillerFull(KanjiKanaTransformer):
    def __init__(self, args, student_params, teacher_checkpoint, alpha=0.5, temperature=2.0):
        super().__init__(args)
        self.student_params = student_params
        self.teacher_checkpoint = teacher_checkpoint
        self.alpha = alpha
        self.temperature = temperature

    def load_teacher(self):

        checkpoint = torch.load(self.teacher_checkpoint, map_location=torch.device(self.args.device))

        teacher_model, optimizer, loss_fn = self.load_by_vocab(checkpoint['src_vocab'],checkpoint['tgt_vocab'],checkpoint["params"])

        teacher_model.load_state_dict(checkpoint['model_state_dict'])
        optimizer.load_state_dict(checkpoint['optimizer_state_dict'])


        teacher_model.to(self.args.device)
        teacher_model.eval()
        return teacher_model

    def load_student(self):
        student_model = KanjiKanaTransformer.load_model(
            num_encoder_layers=self.student_params['num_encoder_layers'],
            num_decoder_layers=self.student_params['num_decoder_layers'],
            emb_size=self.student_params['emb_size'],
            nhead=self.student_params['nhead'],
            src_vocab_size=len(self.vocab_transform[self.args.source_lang]),
            tgt_vocab_size=len(self.vocab_transform[self.args.target_lang]),
            ffn_hid_dim=self.student_params['ffn_hid_dim'],
            dropout=self.student_params['dropout'],
            device=self.args.device
        )
        student_model.to(self.args.device)
        return student_model

    def distill_epoch(self, train_iter, student, teacher, optimizer):
        student.train()
        total_loss = 0
        loss_fn_ce = torch.nn.CrossEntropyLoss(ignore_index=PAD_IDX)
        loss_fn_kl = torch.nn.KLDivLoss(reduction='batchmean')
        train_dataloader = DataLoader(train_iter, batch_size=self.args.batch_size, collate_fn=self.collate_fn, shuffle=True)

        for src, tgt in train_dataloader:
            src = src.to(self.args.device)
            tgt = tgt.to(self.args.device)
            tgt_input = tgt[:-1, :]

            src_mask, tgt_mask, src_padding_mask, tgt_padding_mask = self.create_mask(src, tgt_input)

            with torch.no_grad():
                teacher_logits = teacher(src, tgt_input, src_mask, tgt_mask, src_padding_mask, tgt_padding_mask, src_padding_mask)['logits']

            student_logits = student(src, tgt_input, src_mask, tgt_mask, src_padding_mask, tgt_padding_mask, src_padding_mask)['logits']

            # 蒸留損失
            T = self.temperature
            distill_loss = loss_fn_kl(
                F.log_softmax(student_logits / T, dim=-1),
                F.softmax(teacher_logits / T, dim=-1)
            ) * (T * T)

            # CE損失
            tgt_out = tgt[1:, :]
            ce_loss = loss_fn_ce(student_logits.reshape(-1, student_logits.shape[-1]), tgt_out.reshape(-1))

            # 合計損失
            loss = self.alpha * ce_loss + (1 - self.alpha) * distill_loss

            optimizer.zero_grad()
            loss.backward()
            optimizer.step()

            total_loss += loss.item()

        return total_loss / len(train_dataloader)

    def train_distill(self):
        train_iter = KanjiKanaDataSet(self.args, self.args.train_file)

        # 教師・学生モデル
        teacher = self.load_teacher()
        student = self.load_student()
        optimizer = KanjiKanaTransformer.load_optimizer(student, self.args.lr, self.args.adam_eps)

        writer = SummaryWriter(log_dir=self.args.tensorboard_logdir) if len(self.args.tensorboard_logdir) > 0 else None

        best_loss = None
        patient = 0
        os.makedirs(self.args.output_dir, exist_ok=True)

        for epoch in range(1, self.args.num_epochs + 1):
            start_time = timer()
            train_loss = self.distill_epoch(train_iter, student, teacher, optimizer)
            end_time = timer()

            print(f"[Distill] Epoch {epoch}/{self.args.num_epochs}, Loss: {train_loss:.4f}, Time: {end_time - start_time:.2f}s")

            # チェックポイント保存
            checkpoint_path = os.path.join(self.args.output_dir, f"distill_checkpoint_{epoch:03d}.pt")
            torch.save({
                'epoch': epoch,
                'student_state_dict': student.state_dict(),
                'optimizer_state_dict': optimizer.state_dict(),
                'loss': train_loss
            }, checkpoint_path)

            # TensorBoard
            if writer:
                writer.add_scalar('distill_loss', train_loss, epoch)

            # ベストモデル更新
            if best_loss is None or train_loss < best_loss:
                best_loss = train_loss
                torch.save({
                    'epoch': epoch,
                    'student_state_dict': student.state_dict(),
                    'optimizer_state_dict': optimizer.state_dict(),
                    'loss': train_loss
                }, os.path.join(self.args.output_dir, "distill_checkpoint_best.pt"))
                patient = 0
            else:
                patient += 1

            # 古いチェックポイント削除
            files = sorted(glob.glob(os.path.join(self.args.output_dir, "distill_checkpoint_*.pt")), reverse=True)
            for fname in files[self.args.save_num + 1:]:
                os.remove(fname)

            # 早期停止
            if patient > self.args.earlystop_patient:
                print("Early stopping triggered")
                break

        if writer:
            writer.close()



def main_distill():
    parser = argparse.ArgumentParser(description="Kanji-Kana Transformer Distillation")
    
    # 学生モデルパラメータ
    parser.add_argument('--student_num_encoder_layers', default=4, type=int)
    parser.add_argument('--student_num_decoder_layers', default=4, type=int)
    parser.add_argument('--student_emb_size', default=256, type=int)
    parser.add_argument('--student_n_head', default=4, type=int)
    parser.add_argument('--student_ffn_hid_dim', default=1024, type=int)
    parser.add_argument('--student_dropout', default=0.3, type=float)
    
    # 共通パラメータ
    parser.add_argument('--train_file', default='dataset/dataset.1.8/test.jsonl', type=str)
    parser.add_argument('--valid_file', default='dataset/dataset.1.8/valid.jsonl', type=str)
    parser.add_argument('--batch_size', default=64, type=int)
    parser.add_argument('--num_epochs', default=50, type=int)
    parser.add_argument('--lr', default=2e-5, type=float)
    parser.add_argument('--adam_eps', default=1e-6, type=float)
    parser.add_argument('--earlystop_patient', default=99999, type=int)
    parser.add_argument('--output_dir', default='tmp', type=str)
    parser.add_argument('--tensorboard_logdir', default='logs', type=str)
    parser.add_argument('--prefix', default='translation', type=str)
    parser.add_argument('--source_lang', default='kanji', type=str)
    parser.add_argument('--target_lang', default='kana', type=str)
    parser.add_argument('--save_num', default=1, type=int)
    parser.add_argument('--device', default='mps', choices=('cuda', 'cpu', 'mps'))
    
    # 教師モデル
    parser.add_argument('--model_file', default='tmp/checkpoint_best.pt', type=str)
    
    # 蒸留用ハイパーパラメータ
    parser.add_argument('--alpha', default=0.5, type=float, help="Weight for CE loss in distillation")
    parser.add_argument('--temperature', default=2.0, type=float, help="Temperature for distillation")

    args = parser.parse_args()

    student_params = {
        'num_encoder_layers': args.student_num_encoder_layers,
        'num_decoder_layers': args.student_num_decoder_layers,
        'emb_size': args.student_emb_size,
        'nhead': args.student_n_head,
        'ffn_hid_dim': args.student_ffn_hid_dim,
        'dropout': args.student_dropout
    }

    distiller = KanjiKanaDistillerFull(args, student_params, teacher_checkpoint=args.model_file,
                                       alpha=args.alpha, temperature=args.temperature)

    # トークナイズ・vocab作成済みの train_iter をロード
    distiller.load(KanjiKanaDataSet(args, args.train_file))

    # 蒸留学習開始
    distiller.train_distill()


if __name__ == '__main__':
    main_distill()