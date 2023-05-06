package shop.mtcoding.bank.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;
import shop.mtcoding.bank.config.dummy.DummyObject;
import shop.mtcoding.bank.domain.user.UserRepository;
import shop.mtcoding.bank.dto.account.AccountSaveRequestDto;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@ActiveProfiles("test")
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = WebEnvironment.MOCK)
public class AccountControllerTest extends DummyObject {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper om;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository.save(newUser("ssar", "쌀"));
    }

    //setupBefore = TestExecutionEvent.TEST_EXECUTION : @Test 대상 메서드 실행 전에 수행, @BeforeEach보다는 나중에 실행
    @WithUserDetails(value = "ssar", setupBefore = TestExecutionEvent.TEST_EXECUTION) //DB에서 username = ssar 조회 후 세션에 담아주는 기능을 함, setupBefore=TEST_METHOD (@BeforeEach메서드 실행 전에 수행)
    @Test
    void saveAccount_test() throws Exception {
        //given
        AccountSaveRequestDto accountSaveRequestDto = new AccountSaveRequestDto(9999L, 1234L);
        String requestBody = om.writerWithDefaultPrettyPrinter().writeValueAsString(accountSaveRequestDto);
        System.out.println("requestBody = " + requestBody);

        //when
        ResultActions resultActions = mvc.perform(MockMvcRequestBuilders.post("/api/s/account").content(requestBody).contentType(MediaType.APPLICATION_JSON));

        //then
//        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
//        System.out.println("responseBody = " + responseBody);
        resultActions.andExpect(status().isCreated());
    }
}
