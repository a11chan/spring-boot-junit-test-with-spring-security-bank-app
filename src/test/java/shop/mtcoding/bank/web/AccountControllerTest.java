package shop.mtcoding.bank.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
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
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import shop.mtcoding.bank.config.dummy.DummyObject;
import shop.mtcoding.bank.domain.account.Account;
import shop.mtcoding.bank.domain.account.AccountRepository;
import shop.mtcoding.bank.domain.transaction.Transaction;
import shop.mtcoding.bank.domain.transaction.TransactionEnum;
import shop.mtcoding.bank.domain.transaction.TransactionRepository;
import shop.mtcoding.bank.domain.user.User;
import shop.mtcoding.bank.domain.user.UserRepository;
import shop.mtcoding.bank.dto.account.AccountRequestDto.AccountDepositRequestDto;
import shop.mtcoding.bank.dto.account.AccountRequestDto.AccountSaveRequestDto;
import shop.mtcoding.bank.dto.account.AccountRequestDto.AccountTransferRequestDto;
import shop.mtcoding.bank.dto.account.AccountRequestDto.AccountWithdrawRequestDto;
import shop.mtcoding.bank.handler.ex.CustomApiException;

import javax.persistence.EntityManager;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Sql("classpath:db/teardown.sql") // 테스트 메서드 실행 전 동작
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

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private EntityManager em;

    @BeforeEach
    void setUp() {
        dataSetting();

        // repository의 메서드가 끝날 때마다 flush()되는 건가?
        em.clear();
    }

    private void dataSetting() {
        User ssar = userRepository.save(newUser("ssar", "쌀"));
        User cos = userRepository.save(newUser("cos", "코스,"));
        User love = userRepository.save(newUser("love", "러브"));
        User admin = userRepository.save(newUser("admin", "관리자"));

        Account ssarAccount1 = accountRepository.save(newAccount(1111L, ssar));
        Account cosAccount = accountRepository.save(newAccount(2222L, cos));
        Account loveAccount = accountRepository.save(newAccount(3333L, love));
        Account ssarAccount2 = accountRepository.save(newAccount(4444L, ssar));

        Transaction withdrawTransaction1 = transactionRepository
                .save(newWithdrawTransaction(ssarAccount1, accountRepository));
        Transaction depositTransaction1 = transactionRepository
                .save(newDepositTransaction(cosAccount, accountRepository));
        Transaction transferTransaction1 = transactionRepository
                .save(newTransferTransaction(ssarAccount1, cosAccount, accountRepository));
        Transaction transferTransaction2 = transactionRepository
                .save(newTransferTransaction(ssarAccount1, loveAccount, accountRepository));
        Transaction transferTransaction3 = transactionRepository
                .save(newTransferTransaction(cosAccount, ssarAccount1, accountRepository));
    }

    //setupBefore = TestExecutionEvent.TEST_EXECUTION : @Test 대상 메서드 실행 전에 수행, @BeforeEach보다는 나중에 실행
    @WithUserDetails(value = "ssar", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    //DB에서 username = ssar 조회 후 세션에 담아주는 기능을 함, setupBefore=TEST_METHOD (@BeforeEach메서드 실행 전에 수행)
    @Test
    void saveAccount_test() throws Exception {
        //given
        AccountSaveRequestDto accountSaveRequestDto = new AccountSaveRequestDto(9999L, 1234L);
        String requestBody = om.writerWithDefaultPrettyPrinter().writeValueAsString(accountSaveRequestDto);
        System.out.println("requestBody = " + requestBody);

        //when
        ResultActions resultActions = mvc.perform(post("/api/s/account").content(requestBody).contentType(MediaType.APPLICATION_JSON));

        //then
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("responseBody = " + responseBody);
        resultActions.andExpect(status().isCreated());
    }

    @WithUserDetails(value = "ssar", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    void findUserAccount_Test() throws Exception {
        //given

        //when
        ResultActions resultActions = mvc.perform(get("/api/s/account/login-user"));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("responseBody = " + responseBody);

        //then
        resultActions.andExpect(status().isOk());
    }

    @WithUserDetails(value = "ssar", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    void deleteAccount_test() throws Exception {
        //given
        Long number = 1111L;

        //when
        ResultActions resultActions = mvc.perform(delete("/api/s/account/" + number));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("responseBody = " + responseBody);

        //Junit 테스트에서 delete 쿼리는 DML 관련해서 가장 마지막에 실행되면 발동 안 됨
        //then
        Assertions.assertThrows(CustomApiException.class, () -> accountRepository.findByNumber(number).orElseThrow(() -> new CustomApiException("계좌를 찾을 수 없습니다.")));
    }

    // DummyDevInit.java에 등록된 샘플 객체 생성 실행됨에 주의, 테스트 코드에서 또 같은 객체 생성하지 않기
    @Test
    void depositAccount_test() throws Exception {
        //given
        AccountDepositRequestDto accountDepositRequestDto = new AccountDepositRequestDto(1111L, 1000L, "DEPOSIT", "01012345678");
        String requestBody = om.writerWithDefaultPrettyPrinter().writeValueAsString(accountDepositRequestDto);
        System.out.println("requestBody = " + requestBody);

        //when
        ResultActions resultActions = mvc.perform(post("/api/account/deposit").contentType(MediaType.APPLICATION_JSON).content(requestBody));

        //then
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("responseBody = " + responseBody);
        resultActions.andExpect(status().isCreated());
    }

    @WithUserDetails(value = "ssar", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    void withdrawAccount_test() throws Exception {
        //given
        AccountWithdrawRequestDto withdrawRequest = new AccountWithdrawRequestDto(1111L, 1234L, 100L, TransactionEnum.WITHDRAW.name());
        String requestBody = om.writeValueAsString(withdrawRequest);

        //when
        ResultActions resultActions = mvc.perform(post("/api/s/account/withdraw").contentType(MediaType.APPLICATION_JSON).content(requestBody));

        //then
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("responseBody = " + responseBody);
        resultActions.andExpect(status().isCreated())
                .andExpect(jsonPath("$..balance").value(700));
    }

    @WithUserDetails(value = "ssar", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    void transferAccount_test() throws Exception {
        //given
        AccountTransferRequestDto transferRequest = new AccountTransferRequestDto(1111L, 2222L, 1234L, 100L, TransactionEnum.TRANSFER.name());
        String requestBody = om.writeValueAsString(transferRequest);

        //when
        ResultActions resultActions = mvc.perform(post("/api/s/account/transfer").contentType(MediaType.APPLICATION_JSON).content(requestBody));

        //then
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("responseBody = " + responseBody);
        resultActions.andExpect(status().isCreated())
                .andExpect(jsonPath("$..balance").value(700));
    }

    @WithUserDetails(value = "ssar", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    void findDetailAccount_Test() throws Exception {
        //given
        Long number = 1111L;
        String page = "0";

        //when
        ResultActions resultActions = mvc.perform(get("/api/s/account/" + number).param("page", page));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("responseBody = " + responseBody);

        //then
        resultActions.andExpect(jsonPath("$.data.transactions[0].balance").value(900L));
        resultActions.andExpect(jsonPath("$.data.transactions[1].balance").value(800L));
        resultActions.andExpect(jsonPath("$.data.transactions[2].balance").value(700L));
        resultActions.andExpect(jsonPath("$.data.transactions[3].balance").value(800L));
    }
}