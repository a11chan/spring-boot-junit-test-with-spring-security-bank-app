package shop.mtcoding.bank.config.jwt;

import org.junit.jupiter.api.Test;
import shop.mtcoding.bank.config.auth.LoginUser;
import shop.mtcoding.bank.domain.user.User;
import shop.mtcoding.bank.domain.user.UserEnum;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JwtProcessTest {

    private String createToken() {
        User user = User.builder().id(1L).role(UserEnum.ADMIN).build();
        LoginUser loginUser1 = new LoginUser(user);

        return JwtProcess.create(loginUser1);
    }

    @Test
    public void create_test() throws Exception {
        String jwtToken = createToken();
        System.out.println("jwtToken = " + jwtToken);

        assertTrue(jwtToken.startsWith(JwtVO.TOKEN_PREFIX));
    }

    @Test
    void verify_test() throws Exception {
        //given
        String token = createToken();
        String jwtToken = token.replace(JwtVO.TOKEN_PREFIX, "");

        //when
        LoginUser loginUser2 = JwtProcess.verify(jwtToken);
        System.out.println("loginUser2 = " + loginUser2.getUser().getId());
        System.out.println("loginUser2 = " + loginUser2.getUser().getRole().name());

        //then
        assertThat(loginUser2.getUser().getId()).isEqualTo(1L);
        assertThat(loginUser2.getUser().getRole()).isEqualTo(UserEnum.ADMIN);
    }
}