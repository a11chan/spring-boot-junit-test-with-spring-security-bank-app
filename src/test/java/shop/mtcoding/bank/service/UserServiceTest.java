package shop.mtcoding.bank.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import shop.mtcoding.bank.config.dummy.DummyObject;
import shop.mtcoding.bank.domain.user.User;
import shop.mtcoding.bank.domain.user.UserRepository;
import shop.mtcoding.bank.dto.user.UserRequestDto;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static shop.mtcoding.bank.dto.user.UserResponseDto.JoinResponseDto;

@ExtendWith(value = MockitoExtension.class) // Spring Context 내 Bean이 없는 환경
public class UserServiceTest extends DummyObject {

    @InjectMocks
    private UserService userService;

    // @InjectMocks 객체가 의존 가능하도록 모의 객체를 생성함
    // @InjectMocks 객체에서 사용되는 method stub 정의 필요, 이하 코드 참고
    @Mock
    private UserRepository userRepository;

//    @Spy // @InjectMocks객체에 실제 객체 주입
    @Spy
    private BCryptPasswordEncoder passwordEncoder;

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
}
