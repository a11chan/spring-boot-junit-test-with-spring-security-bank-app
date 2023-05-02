package shop.mtcoding.bank.service;

import lombok.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.mtcoding.bank.domain.user.User;
import shop.mtcoding.bank.domain.user.UserRepository;
import shop.mtcoding.bank.dto.user.UserRequestDto;
import shop.mtcoding.bank.dto.user.UserResponseDto;
import shop.mtcoding.bank.handler.ex.CustomApiException;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final Logger log = LoggerFactory.getLogger(getClass());
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    // 서비스 계층 역할 선언:  Dto를 받고 Dto로 응답한다.
    @Transactional
    public UserResponseDto 회원가입(UserRequestDto userRequestDto) {
        // 1. 동일 유저네임 존재 검사 //Optional 참조변수에 접미사 -OP
        Optional<User> userOP = userRepository.findByUsername(userRequestDto.getUsername());
        if (userOP.isPresent()) throw new CustomApiException("동일한 username이 존재합니다.");

        // 2. 패스워드 인코딩 + 회원가입
        // Persistence Context 에 있던 객체에 접미사 붙임(-PS)
        User userPS = userRepository.save(userRequestDto.toEntity(passwordEncoder));

        // 3. dto 응답
        return new UserResponseDto(userPS);
    }
}
