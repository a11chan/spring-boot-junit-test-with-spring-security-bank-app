package shop.mtcoding.bank.domain.transaction;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import shop.mtcoding.bank.config.dummy.DummyObject;
import shop.mtcoding.bank.domain.account.Account;
import shop.mtcoding.bank.domain.account.AccountRepository;
import shop.mtcoding.bank.domain.user.User;
import shop.mtcoding.bank.domain.user.UserRepository;

import javax.persistence.EntityManager;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles(value = "test")
@DataJpaTest // DB 관련 Bean이 다 올라온다.
public class TransactionRepositoryImplTest extends DummyObject {

    @Autowired
    UserRepository userRepository;

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    TransactionRepository transactionRepository;

    @Autowired
    EntityManager em;

    @BeforeEach
    public void setUp() {
        autoincrementReset();
        dataSetting();
        em.clear(); // dummy 데이터 추가 이후 PC 삭제, 테스트마다 DB에 저장된 값을 사용하기 위함, 레포지토리 테스트 시 필수
    }

    @Test
    void findTransactionList_all_test() throws Exception {
        //given
        Long accountId = 1L;

        //when
        List<Transaction> transactionListPS = transactionRepository.findTransactionList(accountId, "ALL", 0);
        transactionListPS.forEach(transaction -> {
            System.out.println("테스트 : transaction.getId() = " + transaction.getId());
            System.out.println("테스트 : transaction.getTxAmount() = " + transaction.getTxAmount());
            System.out.println("테스트 : transaction.getSender() = " + transaction.getSender());
            System.out.println("테스트 : transaction.getReceiver() = " + transaction.getReceiver());
            System.out.println("테스트 : transaction.getWithdrawAccountBalance() = " + transaction.getWithdrawAccountBalance());
            System.out.println("테스트 : transaction.getDepositAccountBalance() = " + transaction.getDepositAccountBalance());
            System.out.println("테스트 : transaction.getWithdrawAccount().getBalance() = " + transaction.getWithdrawAccount().getBalance());
            System.out.println("테스트 : transaction.getWithdrawAccount().getUser().getFullname() = " + transaction.getWithdrawAccount().getUser().getFullname());
            System.out.println("테스트 : =================");
        });

        //then
        assertThat(transactionListPS.get(3).getDepositAccountBalance()).isEqualTo(800L);
    }

    @Test
    public void dataJpa_test1() {
        List<Transaction> transactionList = transactionRepository.findAll();
        transactionList.forEach(transaction -> {
            System.out.println("테스트 : transaction.getId() = " + transaction.getId());
            System.out.println("테스트 : transaction.getSender() = " + transaction.getSender());
            System.out.println("테스트 : transaction.getReceiver() = " + transaction.getReceiver());
            System.out.println("테스트 : transaction.getGubun() = " + transaction.getGubun());
            System.out.println("테스트 : =================");
        });
    }

    @Test
    public void dataJpa_test2() {
        List<Transaction> transactionList = transactionRepository.findAll();
        transactionList.forEach(transaction -> {
            System.out.println("테스트 : transaction.getId() = " + transaction.getId());
            System.out.println("테스트 : transaction.getSender() = " + transaction.getSender());
            System.out.println("테스트 : transaction.getReceiver() = " + transaction.getReceiver());
            System.out.println("테스트 : transaction.getGubun() = " + transaction.getGubun());
            System.out.println("테스트 : =================");
        });
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

    private void autoincrementReset() {
        em.createNativeQuery("alter table user_tb alter column id restart with 1").executeUpdate();
        em.createNativeQuery("alter table account_tb alter column id restart with 1").executeUpdate();
        em.createNativeQuery("alter table transaction_tb alter column id restart with 1").executeUpdate();
    }
}