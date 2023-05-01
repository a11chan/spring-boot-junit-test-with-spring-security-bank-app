package shop.mtcoding.bank.config;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@AutoConfigureMockMvc // Mock(가상) 환경에 MockMvc가 등록됨
@SpringBootTest(webEnvironment = WebEnvironment.MOCK)
public class SecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void authentication_test() throws Exception {
        //given

        //when
        ResultActions resultActions = mockMvc.perform(get("/api/s/hello"));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        int httpStatusCode = resultActions.andReturn().getResponse().getStatus();
        System.out.println("테스트 = " + responseBody); //JSON 객체가 출력되지 않는 문제 해결 필요 -> SecurityConfig 수정필요
        System.out.println("테스트 = " + httpStatusCode);

        //then
        Assertions.assertThat(httpStatusCode).isEqualTo(401);
    }

    @Test
    void authorization_test() throws Exception {
        //given

        //when
        ResultActions resultActions = mockMvc.perform(get("/api/admin/hello"));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        int httpStatusCode = resultActions.andReturn().getResponse().getStatus();
        System.out.println("테스트 = " + responseBody); //JSON 객체가 출력되지 않는 문제 해결 필요 -> SecurityConfig 수정필요
        System.out.println("테스트 = " + httpStatusCode);

        //then
        Assertions.assertThat(httpStatusCode).isEqualTo(401);
    }
}
