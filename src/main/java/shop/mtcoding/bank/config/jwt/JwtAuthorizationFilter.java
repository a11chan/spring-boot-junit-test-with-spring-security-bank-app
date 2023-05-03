package shop.mtcoding.bank.config.jwt;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import shop.mtcoding.bank.config.auth.LoginUser;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

// 토큰 검증 역할, 모든 주소에서 동작함
public class JwtAuthorizationFilter extends BasicAuthenticationFilter {

    public JwtAuthorizationFilter(AuthenticationManager authenticationManager) {
        super(authenticationManager);
    }

    @Override
    protected void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response, final FilterChain chain) throws IOException, ServletException {
        // 토큰 유효성 확인
        if (isHeaderVerify(request, response)) {
            String token = request.getHeader(JwtVO.HEADER).replace(JwtVO.TOKEN_PREFIX, "");
            LoginUser loginUser = JwtProcess.verify(token);

            // 임시 세션 생성(==강제로그인) // todo null 자리 다른 매개변수로 대체해야 할지 고민
            // 1st 파라미터엔 UserDetails 타입, username만 가능, 현재 메서드에서는 ROLE 정보만 있으면 됨, username이 비어 있지만.
            Authentication authentication = new UsernamePasswordAuthenticationToken(loginUser, null, loginUser.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);

        }
        chain.doFilter(request, response);
    } //이후 SecurityFilterChain에 등록 필 by CustomSecurityFilterManager

    private boolean isHeaderVerify(HttpServletRequest request, HttpServletResponse response) {
        String header = request.getHeader(JwtVO.HEADER);

        return header != null && header.startsWith(JwtVO.TOKEN_PREFIX);
    }
}
