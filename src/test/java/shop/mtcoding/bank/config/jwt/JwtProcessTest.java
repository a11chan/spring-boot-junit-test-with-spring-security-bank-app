package shop.mtcoding.bank.config.jwt;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import shop.mtcoding.bank.config.auth.LoginUser;
import shop.mtcoding.bank.domain.user.User;
import shop.mtcoding.bank.domain.user.UserEnum;

import static org.junit.jupiter.api.Assertions.assertTrue;

class JwtProcessTest {

    @Test
    public void create_test() throws Exception {
        User user = User.builder()
                .id(1L)
                .role(UserEnum.CUSTOMER)
                .build();
        LoginUser loginUser = new LoginUser(user);

        String jwtToken = JwtProcess.create(loginUser);
        System.out.println("jwtToken = " + jwtToken);

        assertTrue(jwtToken.startsWith(JwtVO.TOKEN_PREFIX));
    }

    @Test
    void verify_test() throws Exception {
        //given
        String jwtToken = "eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJiYW5rIiwiZXhwIjoxNjgzNzg2Nzc1LCJpZCI6MSwicm9sZSI6IkNVU1RPTUVSIn0.8mya6pFtar73LY80wNvLL9LJmvosqgrxg9yiYS55pW0KguRJXpQCppSN1Tz9jnyUHS2QOCPbDYOB4HKraoWJ1w";

        //when
        LoginUser loginUser = JwtProcess.verify(jwtToken);
        System.out.println("loginUser = " + loginUser.getUser().getId());

        //then
        Assertions.assertThat(loginUser.getUser().getId()).isEqualTo(1L);
    }
}