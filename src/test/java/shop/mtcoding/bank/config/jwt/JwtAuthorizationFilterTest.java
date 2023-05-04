package shop.mtcoding.bank.config.jwt;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import shop.mtcoding.bank.config.auth.LoginUser;
import shop.mtcoding.bank.domain.user.User;
import shop.mtcoding.bank.domain.user.UserEnum;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles(value = "test")
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = WebEnvironment.MOCK)
class JwtAuthorizationFilterTest {

    @Autowired
    public MockMvc mvc;

    @Test
    void authorization_success_test() throws Exception {
        //given
        User user = User.builder().id(1L).role(UserEnum.CUSTOMER).build();
        LoginUser loginUser = new LoginUser(user);
        String jwtToken = JwtProcess.create(loginUser);
        System.out.println("token = " + jwtToken);

        //when
        ResultActions resultActions = mvc.perform(get("/api/s/user/hello/test").header(JwtVO.HEADER, jwtToken));

        //then
        resultActions.andExpect(status().isNotFound());
    }

    @Test
    void authorization_fail_test() throws Exception {
        ResultActions resultActions = mvc.perform(get("/api/s/user/hello/test"));

        resultActions.andExpect(status().isUnauthorized()); //401
    }

    @Test
    void authorization_admin_test() throws Exception {
        //given
        User user = User.builder().id(1L).role(UserEnum.ADMIN).build();
        LoginUser loginUser = new LoginUser(user);
        String jwtToken = JwtProcess.create(loginUser);
        System.out.println("token = " + jwtToken);

        //when
        ResultActions resultActions = mvc.perform(get("/api/admin/hello/test").header(JwtVO.HEADER, jwtToken));

        //then
        resultActions.andExpect(status().isNotFound());
    }

    @Test
    void authorization_customer_test() throws Exception {
        //given
        User user = User.builder().id(1L).role(UserEnum.CUSTOMER).build();
        LoginUser loginUser = new LoginUser(user);
        String jwtToken = JwtProcess.create(loginUser);
        System.out.println("token = " + jwtToken);

        //when
        ResultActions resultActions = mvc.perform(get("/api/admin/hello/test").header(JwtVO.HEADER, jwtToken));

        //then
        resultActions.andExpect(status().isForbidden());
    }
}