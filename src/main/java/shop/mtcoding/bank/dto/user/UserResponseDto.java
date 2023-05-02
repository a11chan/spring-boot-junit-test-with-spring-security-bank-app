package shop.mtcoding.bank.dto.user;

import lombok.Getter;
import lombok.ToString;
import shop.mtcoding.bank.domain.user.User;
import shop.mtcoding.bank.util.CustomDateUtil;

public class UserResponseDto {

    @Getter
    public static class LoginResponseDto {
        private final Long id;
        private final String username;
        private final String createdAt;

        public LoginResponseDto(User user) {
            this.id = user.getId();
            this.username = user.getUsername();
            this.createdAt = CustomDateUtil.toStringFormat(user.getCreatedAt());
        }
    }

    @ToString
    @Getter
    public static class JoinResponseDto {
        private final Long id;
        private final String username;
        private final String fullname;

        public JoinResponseDto(User user) {
            this.id = user.getId();
            this.username = user.getUsername();
            this.fullname = user.getFullname();
        }
    }
}