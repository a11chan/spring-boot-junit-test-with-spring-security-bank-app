package shop.mtcoding.bank.dto.user;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import shop.mtcoding.bank.domain.user.User;

@Getter
@ToString
@NoArgsConstructor
public class JoinResponseDto {

    private Long id;
    private String username;
    private String fullname;

    public JoinResponseDto(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.fullname = user.getFullname();
    }
}
