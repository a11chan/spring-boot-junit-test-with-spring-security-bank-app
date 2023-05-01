package shop.mtcoding.bank.dto.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import shop.mtcoding.bank.domain.user.User;
import shop.mtcoding.bank.domain.user.UserEnum;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class JoinRequestDto {

    @Pattern(regexp = "^[a-zA-Z0-9]{2,20}$",message = "영문/숫자 2~20자 이내로 작성해주세요")
    @NotEmpty // null, 공백 불가
    private String username;

    @Size(min = 4, max = 20)
    @NotEmpty //길이 4~20자
    private String password;

    @Pattern(regexp = "^[a-zA-Z0-9]{2,6}@[a-zA-Z0-9]{2,6}\\.[a-zA-Z]{2,3}$",message = "email 형식으로 작성해주세요.")
    @NotEmpty //이메일 형식
    private String email;

    @Pattern(regexp = "^[a-zA-Z가-힣]{1,20}$", message = "한글/영문 1~20자 내로 작성해주세요.")
    @NotEmpty //영어,한글,1~20자
    private String fullname;

    public User toEntity(BCryptPasswordEncoder passwordEncoder) { //처음 보는 용법
        return User.builder()
                .username(username)
                .password(passwordEncoder.encode(password))
                .email(email)
                .fullname(fullname)
                .role(UserEnum.CUSTOMER)
                .build();
    }
}
