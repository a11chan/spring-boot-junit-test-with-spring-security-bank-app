package shop.mtcoding.bank.web;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import shop.mtcoding.bank.config.auth.LoginUser;
import shop.mtcoding.bank.dto.ResponseDto;
import shop.mtcoding.bank.dto.account.AccountSaveRequestDto;
import shop.mtcoding.bank.dto.account.AccountSaveResponseDto;
import shop.mtcoding.bank.service.AccountService;
import shop.mtcoding.bank.service.AccountService.AccountListResponseDto;

import javax.validation.Valid;

@RequiredArgsConstructor
@RequestMapping(value = "/api")
@RestController
public class AccountController {

    private final AccountService accountService;

    @PostMapping(value = "/s/account")
    public ResponseEntity<?> saveAccount(@RequestBody @Valid AccountSaveRequestDto accountSaveRequestDto, BindingResult bindingResult, @AuthenticationPrincipal LoginUser loginUser) {
        AccountSaveResponseDto accountSaveResponseDto = accountService.계좌등록(accountSaveRequestDto, loginUser.getUser().getId());

        return new ResponseEntity<>(new ResponseDto<>(1, "계좌등록 성공", accountSaveResponseDto), HttpStatus.CREATED);
    }

//    권한 처리 필요해서 강사님은 선호하지 않음
//    @GetMapping(value = "/s/account/{id}")
//    public ResponseEntity<?> findUserAccount(@PathVariable Long id, @AuthenticationPrincipal LoginUser loginUser) {
//        AccountListResponseDto accountListResponseDto = accountService.계좌목록보기_유저별(id);
//
//        if (id != loginUser.getUser().getId()) {
//            throw new CustomForbiddenException("권한이 없습니다.");
//        }
//
//        return new ResponseEntity<>(new ResponseDto<>(1, "계좌 목록 보기_유저별 성공", accountListResponseDto), HttpStatus.OK);
//    }

    //개발자마다 URI 설계 방식이 다를 수 있음
    //인증 필요, login한 유저의 계좌를 Account 테이블에서 조회
    @GetMapping(value = "/s/account/login-user")
    public ResponseEntity<?> findUserAccount(@AuthenticationPrincipal LoginUser loginUser) {
        AccountListResponseDto accountListResponseDto = accountService.계좌목록보기_유저별(loginUser.getUser().getId());

        return new ResponseEntity<>(new ResponseDto<>(1, "계좌 목록 보기_유저별 성공", accountListResponseDto), HttpStatus.OK);
    }
}