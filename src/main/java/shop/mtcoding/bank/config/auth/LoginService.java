package shop.mtcoding.bank.config.auth;

import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import shop.mtcoding.bank.domain.user.User;
import shop.mtcoding.bank.domain.user.UserRepository;

@Service
public class LoginService implements UserDetailsService {

    private final UserRepository userRepository;

    public LoginService(final UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // 시큐리티로 로그인 시, 시큐리티가 loadUserByUsername() 실행해서 username 확인
    // username이 없으면 오류, 있으면 시큐리티 컨텍스트 내부 세션에 로그인된 세션이 생성됨
    @Override
    public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {
        User userPS = userRepository.findByUsername(username).orElseThrow(
                () -> new InternalAuthenticationServiceException("인증 실패")
        );

        // 로그인 성공 시 Authentication 객체에 담겨 서버 Session 내 SecurityContextHolder에 저장
        // 이후 JWT 토큰 생성 -> response 헤더에 전달됨
        return new LoginUser(userPS);
    }
}
