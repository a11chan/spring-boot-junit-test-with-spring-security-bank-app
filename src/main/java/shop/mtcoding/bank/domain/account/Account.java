package shop.mtcoding.bank.domain.account;


import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import shop.mtcoding.bank.domain.user.User;
import shop.mtcoding.bank.handler.ex.CustomApiException;

import javax.persistence.*;
import java.time.LocalDateTime;

@NoArgsConstructor //스프링은 Empty 생성자로 객체 생성하기 때문에 필요
@Getter
@EntityListeners(AuditingEntityListener.class)
@Table(name = "account_tb")
@Entity
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 4)
    private Long number; // 계좌 번호

    @Column(nullable = false, length = 4)
    private Long password; //계좌 비번

    @Column(nullable = false)
    private Long balance; //잔액(기본값 1000원)

    @ManyToOne(fetch = FetchType.LAZY)
    private User user; // @JoinColumn(name = "user_id") <- 기본값으로 적용됨

    @CreatedDate
    @Column(nullable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Builder
    public Account(Long id,
                   Long number,
                   Long password,
                   Long balance,
                   User user,
                   LocalDateTime createdAt,
                   LocalDateTime updatedAt) {
        this.id = id;
        this.number = number;
        this.password = password;
        this.balance = balance;
        this.user = user;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public void checkOwner(Long userId) {
        if (user.getId() != userId) {
            throw new CustomApiException("계좌 소유자가 아닙니다.");
        }
    }
}
