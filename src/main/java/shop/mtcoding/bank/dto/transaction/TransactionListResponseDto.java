package shop.mtcoding.bank.dto.transaction;

import lombok.Getter;
import shop.mtcoding.bank.domain.account.Account;
import shop.mtcoding.bank.domain.transaction.Transaction;
import shop.mtcoding.bank.util.CustomDateUtil;

import java.util.List;
import java.util.stream.Collectors;

@Getter
public class TransactionListResponseDto {

    private final List<TransactionDto> transactions;

    public TransactionListResponseDto(final Account account, final List<Transaction> transactions) {
        this.transactions = transactions.stream()
                .map(transaction -> new TransactionDto(transaction, account.getNumber()))
                .collect(Collectors.toList());
    }

    @Getter
    public static class TransactionDto {

        private final Long id;
        private final String gubun;
        private final Long amount;
        private final String sender;
        private final String receiver;
        private final String tel;
        private final String createdAt;
        private final Long balance;

        public TransactionDto(Transaction transaction, Long accountNumber) {
            this.id = transaction.getId();
            this.gubun = transaction.getGubun().getValue();
            this.amount = transaction.getTxAmount();
            this.sender = transaction.getSender();
            this.receiver = transaction.getReceiver();
            this.createdAt = CustomDateUtil.toStringFormat(transaction.getCreatedAt());
            this.tel = transaction.getTel() == null ? "없음" : transaction.getTel();

            if (transaction.getDepositAccount() == null) {
                this.balance = transaction.getWithdrawAccountBalance();
            } else if (transaction.getWithdrawAccount() == null) {
                this.balance = transaction.getDepositAccountBalance();
            } else {
                if (accountNumber.equals(transaction.getDepositAccount().getNumber())) {
                    this.balance = transaction.getDepositAccountBalance();
                } else {
                    this.balance = transaction.getWithdrawAccountBalance();
                }
            }
        }
    }
}
