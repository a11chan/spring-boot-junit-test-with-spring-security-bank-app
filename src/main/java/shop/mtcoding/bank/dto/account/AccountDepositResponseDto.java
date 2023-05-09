package shop.mtcoding.bank.dto.account;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import shop.mtcoding.bank.domain.account.Account;
import shop.mtcoding.bank.domain.transaction.Transaction;
import shop.mtcoding.bank.util.CustomDateUtil;

@Getter
public class AccountDepositResponseDto {

    private final Long id; // 계좌 ID
    private final Long number; // 계좌번호
    private final TransactionDto transactionDto; // 거래내역

    public AccountDepositResponseDto(final Account account, final Transaction transaction) {
        this.id = account.getId();
        this.number = account.getNumber();
        this.transactionDto = new TransactionDto(transaction);
    }

    @Getter
    public class TransactionDto {

        private final Long id;
        private final String gubun;
        private final String sender;
        private final String receiver;
        private final Long amount;

        @JsonIgnore // 클라이언트에게 전달 안 함, 서비스 테스트 용
        private final Long depositAccountBalance;
        private final String tel;
        private final String createdAt;

        public TransactionDto(final Transaction transaction) {
            this.id = transaction.getId();
            this.gubun = transaction.getGubun().getValue();
            this.sender = transaction.getSender();
            this.receiver = transaction.getReceiver();
            this.amount = transaction.getAmount();
            this.depositAccountBalance = transaction.getDepositAccountBalance();
            this.tel = transaction.getTel();
            this.createdAt = CustomDateUtil.toStringFormat(transaction.getCreatedAt());
        }
    }
}
