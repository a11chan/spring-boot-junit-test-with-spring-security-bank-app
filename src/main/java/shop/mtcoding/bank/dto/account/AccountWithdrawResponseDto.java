package shop.mtcoding.bank.dto.account;

import lombok.Getter;
import shop.mtcoding.bank.domain.account.Account;
import shop.mtcoding.bank.domain.transaction.Transaction;
import shop.mtcoding.bank.util.CustomDateUtil;

//DTO가 같아도 재사용하지 않기 - DTO 공유되면 잘못될 수 있음, 독립적으로 사용
@Getter
public class AccountWithdrawResponseDto {

    private final Long id; // 계좌 ID
    private final Long number; // 계좌번호
    private final Long balance; // 잔액
    private final TransactionDto transactionDto; // 거래내역

    public AccountWithdrawResponseDto(final Account account, final Transaction transaction) {
        this.id = account.getId();
        this.number = account.getNumber();
        this.balance = account.getBalance();
        this.transactionDto = new TransactionDto(transaction);
    }

    @Getter
    public static class TransactionDto {

        private final Long id;
        private final String gubun;
        private final String sender;
        private final String receiver;
        private final Long amount;
        private final String createdAt;

        public TransactionDto(final Transaction transaction) {
            this.id = transaction.getId();
            this.gubun = transaction.getGubun().getValue();
            this.sender = transaction.getSender();
            this.receiver = transaction.getReceiver();
            this.amount = transaction.getTxAmount();
            this.createdAt = CustomDateUtil.toStringFormat(transaction.getCreatedAt());
        }
    }
}