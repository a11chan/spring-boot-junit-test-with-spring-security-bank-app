package shop.mtcoding.bank.temp;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import shop.mtcoding.bank.dto.account.AccountRequestDto.AccountTransferRequestDto;

public class LongTest {

    @DisplayName(value = "래퍼 클래스 내부 필드값이 같으면 성공")
    @Test
    void long_test() throws Exception {
        //given
        AccountTransferRequestDto transferRequest = new AccountTransferRequestDto(1111L, 1111L, 1234L, 100L, "TRANSFER");

        //when
        boolean equality = transferRequest.getWithdrawNumber().equals(transferRequest.getDepositNumber());

        //then
        Assertions.assertThat(equality).isTrue();
    }
}