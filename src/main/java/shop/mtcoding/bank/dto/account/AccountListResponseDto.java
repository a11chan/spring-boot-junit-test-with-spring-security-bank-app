package shop.mtcoding.bank.dto.account;

import lombok.Getter;
import shop.mtcoding.bank.domain.account.Account;
import shop.mtcoding.bank.domain.user.User;

import java.util.List;
import java.util.stream.Collectors;

@Getter
public class AccountListResponseDto {

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
