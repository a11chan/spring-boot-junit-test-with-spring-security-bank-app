package shop.mtcoding.bank.domain.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    // save - 이미 제공되므로 테스트 생략
    Optional<User> findByUsername(String username);
}
