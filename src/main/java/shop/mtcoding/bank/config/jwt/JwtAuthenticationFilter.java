package shop.mtcoding.bank.config.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import shop.mtcoding.bank.config.auth.LoginUser;
import shop.mtcoding.bank.util.CustomResponseUtil;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;

import static shop.mtcoding.bank.dto.user.UserRequestDto.*;
import static shop.mtcoding.bank.dto.user.UserResponseDto.*;

public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private final AuthenticationManager authenticationManager;

    public JwtAuthenticationFilter(final AuthenticationManager authenticationManager) {
        super(authenticationManager);
        this.authenticationManager = authenticationManager;
        setFilterProcessesUrl("/api/login");
    }

    @Override //Post /login 요청 시 동작
    public Authentication attemptAuthentication(final HttpServletRequest request, final HttpServletResponse response) throws AuthenticationException {
        log.debug("디버그 : attemptAuthentication 호출됨");

        try {
            ObjectMapper om = new ObjectMapper();
            LoginRequestDto loginRequestDto = om.readValue(request.getInputStream(), LoginRequestDto.class);

            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                    loginRequestDto.getUsername(), loginRequestDto.getPassword());
            // 강제 로그인 진행, JWT를 쓴다고 해도 Controller 계층에 진입 시
            // 편리한 시큐리티의 인증, 권한 확인 기능을 사용하기 위해 세션을 만든다.
            // 이 세션의 유효기간은 request, response 하면 끝

            return authenticationManager.authenticate(authenticationToken); // UserDetailsService의 loadUserByUsername 호출
        } catch (Exception e) {
            // ControllerAdvice를 거치기 전 예외처리 됨
            // SecurityConfig.java -> authenticationEntryPoint에 걸림
            throw new InternalAuthenticationServiceException(e.getMessage());
        }
    }

    @Override // 인증 성공 시 동작
    protected void successfulAuthentication(final HttpServletRequest request, final HttpServletResponse response, final FilterChain chain, final Authentication authResult) throws IOException, ServletException {
        log.debug("디버그 : successfulAuthentication 호출됨");

        LoginUser loginUser = (LoginUser) authResult.getPrincipal();
        String jwtToken = JwtProcess.create(loginUser);
        response.addHeader(JwtVO.HEADER, jwtToken);

        LoginResponseDto loginResponseDto = new LoginResponseDto(loginUser.getUser());
        CustomResponseUtil.success(response, loginResponseDto);
    }
}
