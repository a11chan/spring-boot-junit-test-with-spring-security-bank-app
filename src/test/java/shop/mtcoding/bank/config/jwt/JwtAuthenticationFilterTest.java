package shop.mtcoding.bank.config.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;
import shop.mtcoding.bank.config.dummy.DummyObject;
import shop.mtcoding.bank.domain.user.UserRepository;
import shop.mtcoding.bank.dto.user.UserRequestDto.LoginRequestDto;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@ActiveProfiles(value = "test")
//Mockito 환경(가상 환경)에서 MockMvc @Autowired 적용 가능하게 함
@AutoConfigureMockMvc
//가상 환경에 스프링 컴포넌트 올려서 Object Mapper가 Autowired 되게 함
@SpringBootTest(webEnvironment = WebEnvironment.MOCK)
public class JwtAuthenticationFilterTest extends DummyObject {

    @Autowired
    public ObjectMapper om;

    @Autowired
    public MockMvc mvc;

    @Autowired
    public UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository.save(newUser("ssar", "쌀"));
    }

    @Test
    void successfulAuthentication_test() throws Exception {
        //given
        LoginRequestDto loginRequestDto = new LoginRequestDto("ssar","1234");
        String requestBody = om.writerWithDefaultPrettyPrinter().writeValueAsString(loginRequestDto);
        System.out.println("requestBody = " + requestBody);

        //when
        ResultActions resultActions = mvc.perform(post("/api/login").contentType(MediaType.APPLICATION_JSON).content(requestBody));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        String jwtToken = resultActions.andReturn().getResponse().getHeader(JwtVO.HEADER);
        System.out.println("responseBody = " + responseBody);
        System.out.println("jwtToken = " + jwtToken);

        //then
        resultActions.andExpect(status().isOk());
        assertNotNull(jwtToken);
        assertTrue(jwtToken.startsWith(JwtVO.TOKEN_PREFIX));
        resultActions.andExpect(jsonPath("$.data.username").value("ssar"));
    }

    @Test
    void unsuccessfulAuthentication_test() throws Exception {
        //given
        LoginRequestDto loginRequestDto = new LoginRequestDto("ssar","12345");
        String requestBody = om.writerWithDefaultPrettyPrinter().writeValueAsString(loginRequestDto);
        System.out.println("requestBody = " + requestBody);

        //when
        ResultActions resultActions = mvc.perform(post("/api/login").contentType(MediaType.APPLICATION_JSON).content(requestBody));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        String jwtToken = resultActions.andReturn().getResponse().getHeader(JwtVO.HEADER);
        System.out.println("responseBody = " + responseBody);
        System.out.println("jwtToken = " + jwtToken);

        //then
        resultActions.andExpect(status().isUnauthorized());
    }
}
