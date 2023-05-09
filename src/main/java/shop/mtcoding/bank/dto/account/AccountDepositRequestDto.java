package shop.mtcoding.bank.dto.account;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AccountDepositRequestDto {
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
