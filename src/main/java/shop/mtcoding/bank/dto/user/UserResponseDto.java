package shop.mtcoding.bank.dto.user;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import shop.mtcoding.bank.domain.user.User;
import shop.mtcoding.bank.util.CustomDateUtil;

@Getter
@ToString
@NoArgsConstructor
public class UserResponseDto {

    @Getter
    public static class LoginResponseDto {

        private static Long id;
        private static String username;
        private static String createdAt;

        public LoginResponseDto(User user) {
            this.id = user.getId();
            this.username = user.getUsername();
            this.createdAt = CustomDateUtil.toStringFormat(user.getCreatedAt());
            //날짜 변환 util 클래스로 넘기기
        }
    }

    private Long id;
    private String username;
    private String fullname;

    public UserResponseDto(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.fullname = user.getFullname();
    }
}
