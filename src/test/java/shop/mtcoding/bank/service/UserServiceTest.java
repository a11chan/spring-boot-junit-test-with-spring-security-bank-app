package shop.mtcoding.bank.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import shop.mtcoding.bank.config.dummy.DummyObject;
import shop.mtcoding.bank.domain.account.Account;
import shop.mtcoding.bank.domain.account.AccountRepository;
import shop.mtcoding.bank.domain.user.User;
import shop.mtcoding.bank.domain.user.UserRepository;
import shop.mtcoding.bank.dto.account.AccountListResponseDto;
import shop.mtcoding.bank.dto.user.UserRequestDto;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static shop.mtcoding.bank.dto.user.UserResponseDto.JoinResponseDto;

@ExtendWith(value = MockitoExtension.class) // Spring Context 내 Bean이 없는 환경
public class UserServiceTest extends DummyObject {

    @InjectMocks
    private UserService userService;

    @InjectMocks
    private AccountService accountService;

    // @InjectMocks 객체가 의존 가능하도록 모의 객체를 생성함
    // @InjectMocks 객체에서 사용되는 method stub 정의 필요, 이하 코드 참고
    @Mock
    private UserRepository userRepository;

    @Mock
    private AccountRepository accountRepository;

    //    @Spy // @InjectMocks객체에 실제 객체 주입
    @Spy
    private BCryptPasswordEncoder passwordEncoder;

    @Test
    void 계좌목록보기_유저별_test() {
        //given
        Long userId = 1L;

        // stub
        User ssar = newMockUser(1L, "ssar", "쌀");
        Account ssarAccount1 = newMockAccount(1L, 1111L, 1000L, ssar);
        Account ssarAccount2 = newMockAccount(2L, 1234L, 1000L, ssar);
        List<Account> accountList = Arrays.asList(ssarAccount1, ssarAccount2);
        when(accountRepository.findByUser_id(any())).thenReturn(accountList);

        //when
        AccountListResponseDto accountListResponseDto = accountService.계좌목록보기_유저별(userId);

        //then
        assertThat(accountListResponseDto.getFullname()).isEqualTo("쌀");
        assertThat(accountListResponseDto.getAccounts().size()).isEqualTo(2);
    }

    @Test
    void 회원가입_test() {
        //given
        UserRequestDto userRequestDto = new UserRequestDto(
                "ssar",
                "1234",
                "ssar@nate.com",
                "쌀");

        // method stub 정의
        when(userRepository.findByUsername(any())).thenReturn(Optional.empty());
//        when(userRepository.findByUsername(any())).thenReturn(Optional.of(new User()));

        // 미리 정의한 User 객체 리턴
        User ssar = newMockUser(1L, "ssar", "ssar");
        when(userRepository.save(any())).thenReturn(ssar);

        //when
        JoinResponseDto joinResponseDto = userService.회원가입(userRequestDto);
        System.out.println("joinResponseDto = " + joinResponseDto);

        //then
        assertThat(joinResponseDto.getId()).isEqualTo(1L);
        assertThat(joinResponseDto.getUsername()).isEqualTo("ssar");
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
        });
    }
}
