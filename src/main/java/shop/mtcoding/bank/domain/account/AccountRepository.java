package shop.mtcoding.bank.domain.account;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {

    // checkpoint: User 정보가 Join되어 같이 조회되도록 수정 필요(fetch join)
    // @Query("select ac from Account ac join fetch ac.user u where ac.number = :number")
    Optional<Account> findByNumber(Long number);

    // select * from account where user_id = :id
    List<Account> findByUser_id(Long id);
}
