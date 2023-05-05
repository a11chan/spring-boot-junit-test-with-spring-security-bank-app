package shop.mtcoding.bank.dto.account;

import lombok.Getter;
import shop.mtcoding.bank.domain.account.Account;

@Getter
public class AccountSaveResponseDto {

    private final Long id;
    private final Long number;
    private final Long balance;

    public AccountSaveResponseDto(final Account account) {
        this.id = account.getId();
        this.number = account.getNumber();
        this.balance = account.getBalance();
    }
}
