package shop.mtcoding.bank.temp;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.regex.Pattern;

public class RegexTest {

    @Test
    void 한글만가능() {
        //given
        String value = "ㄱr";

        //when
        boolean result = Pattern.matches("^[ㄱ-ㅎ가-힣]+$", value);

        //then
        Assertions.assertThat(result).isFalse();
    }

    @Test
    void 한글은안됨() {
        //given
        String value = "ㄱㄴ";

        //when
        boolean result = Pattern.matches("^[^ㄱ-ㅎ가-힣]+$", value);

        //then
        Assertions.assertThat(result).isFalse();
    }

    @Test
    void 영어만가능() {
        //given
        String value = "abcㄱ";

        //when
        boolean result = Pattern.matches("^[a-zA-Z]+$", value);

        //then
        Assertions.assertThat(result).isFalse();
    }

    @Test
    void 영어는안됨() {
        //given
        String value = "r가";

        //when
        boolean result = Pattern.matches("^[^a-zA-Z]*$", value);

        //then
        Assertions.assertThat(result).isFalse();
    }

    @Test
    void 영어숫자만가능() {
        //given
        String value = "ㄱ*";

        //when
        boolean result = Pattern.matches("^[a-zA-Z0-9]+$", value);

        //then
        Assertions.assertThat(result).isFalse();
    }

    @Test
    void 영어만가능최소2최대4자() {
        //given
        String value = "ab가";

        //when
        boolean result = Pattern.matches("^[a-zA-Z]{2,4}$", value);

        //then
        Assertions.assertThat(result).isFalse();
    }

    // username, email, fullname 테스트 진행
    @Test
    void user_username_test() {
        String username = "ㄱㄱ";
        boolean result = Pattern.matches("^[a-zA-Z0-9]{2,20}$", username);
        System.out.println("result = " + result);
    }

    @Test
    void user_fullname_test() {
        String fullname = "별";
        boolean result = Pattern.matches("^[a-zA-Z가-힣]{1,20}$", fullname);
        System.out.println("result = " + result);
    }

    @Test
    void user_email_test() {
        String email = "Aa1b@c1.net";
        boolean result = Pattern.matches("^[a-zA-Z0-9]{2,10}@[a-zA-Z0-9]{2,6}\\.[a-zA-Z]{2,3}$", email);
        System.out.println("result = " + result);
    }
}
