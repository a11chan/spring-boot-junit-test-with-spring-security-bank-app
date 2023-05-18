package shop.mtcoding.bank.dto.account;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import shop.mtcoding.bank.domain.account.Account;
import shop.mtcoding.bank.domain.transaction.Transaction;
import shop.mtcoding.bank.domain.user.User;
import shop.mtcoding.bank.util.CustomDateUtil;

import java.util.List;
import java.util.stream.Collectors;

public class AccountResponseDto {

    @Getter
    public static class AccountDepositResponseDto {

        private final Long id; // 계좌 ID
        private final Long number; // 계좌번호
        private final TransactionDto transactionDto; // 거래내역

        public AccountDepositResponseDto(final Account account, final Transaction transaction) {
            this.id = account.getId();
            this.number = account.getNumber();
            this.transactionDto = new TransactionDto(transaction);
        }

        @Getter
        public static class TransactionDto {

            private final Long id;
            private final String gubun;
            private final String sender;
            private final String receiver;
            private final Long amount;
            private final Long depositAccountBalance;
            private final String tel;
            private final String createdAt;

            public TransactionDto(final Transaction transaction) {
                this.id = transaction.getId();
                this.gubun = transaction.getGubun().getValue();
                this.sender = transaction.getSender();
                this.receiver = transaction.getReceiver();
                this.amount = transaction.getTxAmount();
                this.depositAccountBalance = transaction.getDepositAccountBalance();
                this.tel = transaction.getTel();
                this.createdAt = CustomDateUtil.toStringFormat(transaction.getCreatedAt());
            }
        }
    }

    @Getter
    public static class AccountListResponseDto {

        private final String fullname;
        private final List<AccountDto> accounts;

        public AccountListResponseDto(final User user, final List<Account> accounts) {
            this.fullname = user.getFullname();
            this.accounts = accounts.stream().map(AccountDto::new).collect(Collectors.toList());
        }

        @Getter
        public static class AccountDto {

            private final Long id;
            private final Long number;
            private final Long balance;

            public AccountDto(final Account account) {
                this.id = account.getId();
                this.number = account.getNumber();
                this.balance = account.getBalance();
            }
        }
    }

    @Getter
    public static class AccountSaveResponseDto {

        private final Long id;
        private final Long number;
        private final Long balance;

        public AccountSaveResponseDto(final Account account) {
            this.id = account.getId();
            this.number = account.getNumber();
            this.balance = account.getBalance();
        }
    }

    //DTO가 같아도 재사용하지 않기 - DTO 공유되면 잘못될 수 있음, 독립적으로 사용
    @Getter
    public static class AccountWithdrawResponseDto {

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

    @Getter
    public static class AccountTransferResponseDto {

        private final Long id; // 계좌 ID
        private final Long number; // 계좌번호
        private final Long balance; // 출금 계좌잔액
        private final TransactionDto transactionDto; // 거래내역

        public AccountTransferResponseDto(final Account account, final Transaction transaction) {
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
            private final Long txAmount;
            @JsonIgnore
            private final Long depositAccountBalance;
            private final String createdAt;

            public TransactionDto(final Transaction transaction) {
                this.id = transaction.getId();
                this.gubun = transaction.getGubun().getValue();
                this.sender = transaction.getSender();
                this.receiver = transaction.getReceiver();
                this.txAmount = transaction.getTxAmount();
                this.depositAccountBalance = transaction.getDepositAccountBalance();
                this.createdAt = CustomDateUtil.toStringFormat(transaction.getCreatedAt());
            }
        }
    }

    @Getter
    public static class AccountDetailResponseDto {

        private final Long id; // accountId
        private final Long number; // accountNumber
        private final Long balance; // 계좌 최종 잔액
        private final List<TransactionDto> transactions;

        public AccountDetailResponseDto(final Account account, final List<Transaction> transactions) {
            this.id = account.getId();
            this.number = account.getNumber();
            this.balance = account.getBalance();
            this.transactions = transactions.stream()
                    .map(transaction -> new TransactionDto(transaction, account.getNumber()))
                    .collect(Collectors.toList());
        }

        //다른 곳에 있는 Dto를 재사용하지 않고 내부 클래스 활용
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
}
