package shop.mtcoding.bank.dto.account;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import shop.mtcoding.bank.domain.account.Account;
import shop.mtcoding.bank.domain.user.User;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

public class AccountRequestDto {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AccountDepositRequestDto {
        @NotNull
        @Digits(integer = 4, fraction = 4)
        private Long number;

        @NotNull
        private Long amount;

        @NotEmpty
        @Pattern(regexp = "^(DEPOSIT)$")
        private String gubun;

        @NotEmpty
        @Pattern(regexp = "^[0-9]{11}")
        private String tel;
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class AccountSaveRequestDto {

        @NotNull
        @Digits(integer = 4, fraction = 4)
        private Long number;

        @NotNull
        @Digits(integer = 4, fraction = 4)
        private Long password;

        public Account toEntity(User user) {
            return Account.builder()
                    .number(number)
                    .password(password)
                    .balance(1000L)
                    .user(user)
                    .build();
        }
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class AccountWithdrawRequestDto {

        @NotNull
        @Digits(integer = 4, fraction = 4)
        private Long number;

        @NotNull
        @Digits(integer = 4, fraction = 4)
        private Long password;

        @NotNull
        private Long amount;

        @NotEmpty
        @Pattern(regexp = "^(WITHDRAW)$")
        private String gubun;
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    public static class AccountTransferRequestDto {

        @NotNull
        @Digits(integer = 4, fraction = 4)
        private Long withdrawNumber;

        @NotNull
        @Digits(integer = 4, fraction = 4)
        private Long depositNumber;

        @NotNull
        @Digits(integer = 4, fraction = 4)
        private Long withdrawPassword;

        @NotNull
        private Long txAmount;

        @NotEmpty
        @Pattern(regexp = "TRANSFER")
        private String gubun;
    }
}
