package shop.mtcoding.bank.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import shop.mtcoding.bank.config.dummy.DummyObject;
import shop.mtcoding.bank.domain.account.Account;
import shop.mtcoding.bank.domain.account.AccountRepository;
import shop.mtcoding.bank.domain.user.User;
import shop.mtcoding.bank.domain.user.UserRepository;
import shop.mtcoding.bank.dto.account.AccountListResponseDto;
import shop.mtcoding.bank.dto.account.AccountSaveRequestDto;
import shop.mtcoding.bank.dto.account.AccountSaveResponseDto;
import shop.mtcoding.bank.handler.ex.CustomApiException;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AccountServiceTest extends DummyObject {

    @InjectMocks
    private AccountService accountService;

    @Mock //@InjectMock에 주입될 객체 지정
    private UserRepository userRepository;

    @Mock
    private AccountRepository accountRepository;

    @Spy //실제 객체를 @InjectMock에 주입
    private ObjectMapper om;

    @Test
    void 계좌등록() throws Exception {
        //given
        Long userId = 1L;
        AccountSaveRequestDto accountSaveRequestDto = new AccountSaveRequestDto(1111L, 1234L);

        //stub1
        User ssar = newMockUser(userId, "ssar", "쌀");
        when(userRepository.findById(any())).thenReturn(Optional.of(ssar));

        //stub2
        when(accountRepository.findByNumber(any())).thenReturn(Optional.empty());

        //stub3 //계좌 등록 결과(expected)라서 id 1L을 위의 userId로 대체하지 않음
        Account ssarAccount = newMockAccount(1L, 1111L, 1000L, ssar);
        when(accountRepository.save(any())).thenReturn(ssarAccount);

        //when
        AccountSaveResponseDto accountSaveResponseDto = accountService.계좌등록(accountSaveRequestDto, userId);
        String responseBody = om.writerWithDefaultPrettyPrinter().writeValueAsString(accountSaveResponseDto);
        System.out.println("responseBody = " + responseBody);

        //then
        assertThat(accountSaveResponseDto.getNumber()).isEqualTo(1111L);
    }

    @Test
    void 계좌목록보기_유저별_test() {
        // given
        Long userId = 1L;

        // stub
        User ssar = newMockUser(1L, "ssar", "쌀");
        when(userRepository.findById(userId)).thenReturn(Optional.of(ssar));

        Account ssarAccount1 = newMockAccount(1L, 1111L, 1000L, ssar);
        Account ssarAccount2 = newMockAccount(2L, 1234L, 1000L, ssar);
        List<Account> accountList = Arrays.asList(ssarAccount1, ssarAccount2);
        when(accountRepository.findByUser_id(any())).thenReturn(accountList);

        //when
        AccountListResponseDto accountListResponseDto = accountService.계좌목록보기_유저별(userId);

        //then
        Assertions.assertThat(accountListResponseDto.getFullname()).isEqualTo("쌀");
        Assertions.assertThat(accountListResponseDto.getAccounts().size()).isEqualTo(2);
    }

    @Test
    void 계좌삭제_test() throws Exception {
        //given
        Long number = 1111L;
        Long userId = 2L; //삭제 시 반환값이 없으므로 유저 아이디가 다를 경우 검증

        //stub (가정)
        User ssar = newMockUser(1L, "ssar", "쌀");
        Account ssarAccount = newMockAccount(1L, 1111L, 1000L, ssar);
        when(accountRepository.findByNumber(any())).thenReturn(Optional.of(ssarAccount));

        //when

        //then
        assertThatThrownBy(() -> {
            accountService.계좌삭제(ssarAccount.getNumber(), userId);
        }).isInstanceOf(CustomApiException.class);
    }
}
