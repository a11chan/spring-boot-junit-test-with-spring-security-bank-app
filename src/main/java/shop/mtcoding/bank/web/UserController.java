package shop.mtcoding.bank.web;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import shop.mtcoding.bank.dto.ResponseDto;
import shop.mtcoding.bank.dto.user.UserRequestDto;
import shop.mtcoding.bank.service.UserService;

import javax.validation.Valid;

import static shop.mtcoding.bank.dto.user.UserResponseDto.JoinResponseDto;

@RequiredArgsConstructor
@RequestMapping(value = "/api")
@RestController
public class UserController {

    private final UserService userService;

    @PostMapping(value = "/join")
    public ResponseEntity<?> join(
            @RequestBody @Valid UserRequestDto userRequestDto,
            BindingResult bindingResult) {

        JoinResponseDto joinResponseDto = userService.회원가입(userRequestDto);

        return new ResponseEntity<>(new ResponseDto(1, "회원가입 성공", joinResponseDto), HttpStatus.CREATED);
    }
}

