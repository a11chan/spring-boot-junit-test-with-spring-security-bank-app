package shop.mtcoding.bank.config.jwt;

// reFresh 토큰은 구현하지 않음 - 강의 분량 제한
public class JwtVO {

    // HS256 사용 - 서버에서만 키를 검증하므로 대칭키 1개 사용
    // 노출되지 않도록 관리 - 클라우드의 환경변수를 통해 꺼내 쓸 수 있도록 관리, 멘토님 깃헙 참고
    public static final String SECRET = "메타코딩";
    public static final int EXPIRATION_TIME = 1000 * 60 * 60 * 24 * 7; // 일주일
    public static final String TOKEN_PREFIX = "Bearer "; // space 1개 마지막에 필요
    public static final String HEADER = "Authorization";
}
