package shop.mtcoding.bank.config.dummy;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import shop.mtcoding.bank.domain.account.Account;
import shop.mtcoding.bank.domain.account.AccountRepository;
import shop.mtcoding.bank.domain.transaction.Transaction;
import shop.mtcoding.bank.domain.transaction.TransactionEnum;
import shop.mtcoding.bank.domain.user.User;
import shop.mtcoding.bank.domain.user.UserEnum;

import java.time.LocalDateTime;

public class DummyObject {

    protected Transaction newTransferTransaction(Account withdrawAccount, Account depositAccount, AccountRepository accountRepository) {
        withdrawAccount.withdraw(100L);
        depositAccount.deposit(100L);

        //Service 레이어에서 도메인 객체가 변경된 것이 아니어서 티체킹이 안 되기 때문에 직접 반영
        if (accountRepository != null) {
            accountRepository.save(withdrawAccount);
            accountRepository.save(depositAccount);
        }

        return Transaction.builder()
                .depositAccount(depositAccount)
                .withdrawAccount(withdrawAccount)
                .depositAccountBalance(depositAccount.getBalance())
                .withdrawAccountBalance(withdrawAccount.getBalance())
                .txAmount(100L)
                .gubun(TransactionEnum.TRANSFER)
                .sender(withdrawAccount.getNumber() + "")
                .receiver(depositAccount.getNumber() + "")
                .build();
    }

    protected Transaction newDepositTransaction(Account account, AccountRepository accountRepository) {
        account.deposit(100L); // 잔액 900원
        //Service 레이어에서 도메인 객체가 변경된 것이 아니어서 티체킹이 안 되기 때문에 직접 반영
        if (accountRepository != null) {
            accountRepository.save(account);
        }

        return Transaction.builder()
                .depositAccount(account)
                .withdrawAccount(null)
                .depositAccountBalance(account.getBalance())
                .withdrawAccountBalance(null)
                .txAmount(100L)
                .gubun(TransactionEnum.DEPOSIT)
                .sender("ATM")
                .receiver(account.getNumber() + "")
                .tel("01012345678")
                .build();
    }

    protected Transaction newWithdrawTransaction(Account account, AccountRepository accountRepository) {
        account.withdraw(100L); // 잔액 should be 1100원
        //Service 레이어에서 도메인 객체가 변경된 것이 아니어서 티체킹이 안 되기 때문에 직접 반영
        if (accountRepository != null) {
            accountRepository.save(account);
        }

        return Transaction.builder()
                .depositAccount(null)
                .withdrawAccount(account)
                .depositAccountBalance(null)
                .withdrawAccountBalance(account.getBalance())
                .txAmount(100L)
                .gubun(TransactionEnum.WITHDRAW)
                .sender(account.getNumber() + "")
                .receiver("ATM")
                .build();
    }

    protected static Transaction newMockDepositTransaction(Long id, Account account) {
        account.deposit(100L);

        return Transaction.builder()
                .id(id)
                .withdrawAccount(null)
                .withdrawAccountBalance(null)
                .depositAccount(account)
                .depositAccountBalance(account.getBalance())
                .txAmount(100L)
                .gubun(TransactionEnum.DEPOSIT)
                .sender("ATM")
                .receiver(account.getNumber() + "")
                .tel("01012345678")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    protected User newUser(String username, String fullname) { //UserResponseDto로 쓰일 예정이라 일부 필드 불필요하여 삭제
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encPassword = passwordEncoder.encode("1234");
        return User.builder()
                .username(username)
                .password(encPassword)
                .email(username + "@nate.com")
                .fullname(fullname)
                .role(UserEnum.CUSTOMER)
                .build();
    }

    protected User newMockUser(Long id, String username, String fullname) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encPassword = passwordEncoder.encode("1234");
        return User.builder()
                .id(id)
                .username(username)
                .password(encPassword)
                .email(username + "@nate.com")
                .fullname(fullname)
                .role(UserEnum.CUSTOMER)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    protected Account newAccount(Long number, User user) {
        return Account.builder()
                .number(number)
                .password(1234L)
                .balance(1000L)
                .user(user)
                .build();
    }

    protected Account newMockAccount(Long id, Long number, Long balance, User user) {
        return Account.builder()
                .id(id)
                .number(number)
                .password(1234L)
                .balance(balance)
                .user(user)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }
}