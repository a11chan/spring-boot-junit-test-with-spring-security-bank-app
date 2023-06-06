package shop.mtcoding.bank.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import shop.mtcoding.bank.config.jwt.JwtAuthenticationFilter;
import shop.mtcoding.bank.config.jwt.JwtAuthorizationFilter;
import shop.mtcoding.bank.domain.user.UserEnum;
import shop.mtcoding.bank.util.CustomResponseUtil;

//@Slf4j //Junit 테스트 시 문제 발생하므로 직접 필드에 선언
@Configuration
public class SecurityConfig {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Bean //@Configuration이 있는 클래스에서만 작동, IoC 컨테이너에 등록
    public BCryptPasswordEncoder passwordEncoder() {
        log.debug("디버그 : BCryptPasswordEncoder 빈 등록됨");

        return new BCryptPasswordEncoder();
    }

    // JWT 필터 등록이 필요함
    public static class CustomSecurityFilterManager extends AbstractHttpConfigurer<CustomSecurityFilterManager, HttpSecurity> {
        @Override
        public void configure(final HttpSecurity builder) throws Exception {
            // 강제 세션 로그인을 위한 AuthenticationManager 획득, 주입
            AuthenticationManager authenticationManager = builder.getSharedObject(AuthenticationManager.class);
            builder.addFilter(new JwtAuthenticationFilter(authenticationManager));
            builder.addFilter(new JwtAuthorizationFilter(authenticationManager));
            super.configure(builder);
        }
    }

    // 2. JWT 필터 등록
    // 1. JWT 서버 생성(Session 미사용)
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        log.debug("디버그: filterChain 빈 등록됨");

        //보안 관리
        http.headers().frameOptions().disable(); // iframe 미허용
        http.csrf().disable(); // enable 이면 postman 작동 안 함
        http.cors().configurationSource(configurationSource()); //cors 허용

        //상태-인증 관리
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS); //JsessionID를 서버에서 미관리
        http.formLogin().disable(); // <form> 형태의 로그인 미사용, react or API로 대체
        http.httpBasic().disable(); // 팝업창을 이용한 브라우저의 사용자 인증을 차단

        //custom 필터 적용
        http.apply(new CustomSecurityFilterManager());

        //인증 실패 Exception 처리
        http.exceptionHandling().authenticationEntryPoint((request, response, authException) -> CustomResponseUtil.fail(response, "로그인 실패", HttpStatus.UNAUTHORIZED));

        //권한 없음 Exception 처리
        http.exceptionHandling().accessDeniedHandler((request, response, e) -> CustomResponseUtil.fail(response, "권한 없음", HttpStatus.FORBIDDEN));

        //인가(권한) 관리
        http.authorizeRequests()
                .antMatchers("/api/s/**").authenticated() //인증 성공 시 404
                .antMatchers("/api/admin/**").hasRole("" + UserEnum.ADMIN) //최근 공식문서에 따르면 ROLE_ 생략 가능
                .anyRequest().permitAll();

        return http.build();
    }

    public CorsConfigurationSource configurationSource() {
        log.debug("디버그: cors설정이 SecurityFilterChain에 등록됨");

        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedHeader("*"); //모든 HTTP 헤더 허용
        configuration.addAllowedMethod("*"); //모든 HTTP 메서드 허용
        configuration.addAllowedOriginPattern("*"); //모든 IP 주소 허용(or FE IP만 허용, 등 필요에 따라 수정)
        configuration.setAllowCredentials(true); //클라이언트의 쿠키 요청/응답 허용
        configuration.addExposedHeader("Authorization"); //예전엔 기본값으로 추가됐지만 지금은 아님

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); //모든 요청에 대해 위 단락에서 설정한 내용 적용

        return source;
    }
}
