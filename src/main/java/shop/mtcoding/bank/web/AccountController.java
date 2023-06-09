package shop.mtcoding.bank.web;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import shop.mtcoding.bank.config.auth.LoginUser;
import shop.mtcoding.bank.dto.ResponseDto;
import shop.mtcoding.bank.dto.account.AccountRequestDto.AccountDepositRequestDto;
import shop.mtcoding.bank.dto.account.AccountRequestDto.AccountSaveRequestDto;
import shop.mtcoding.bank.dto.account.AccountRequestDto.AccountTransferRequestDto;
import shop.mtcoding.bank.dto.account.AccountRequestDto.AccountWithdrawRequestDto;
import shop.mtcoding.bank.dto.account.AccountResponseDto.*;
import shop.mtcoding.bank.service.AccountService;

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

    @DeleteMapping(value = "/s/account/{number}")
    public ResponseEntity<?> deleteAccount(@PathVariable Long number, @AuthenticationPrincipal LoginUser loginuser) {
        accountService.계좌삭제(number, loginuser.getUser().getId());
        return new ResponseEntity<>(new ResponseDto<>(1, "계좌 삭제 완료", null), HttpStatus.OK);
    }

    @PostMapping("/account/deposit")
    public ResponseEntity<?> depositAccount(@RequestBody @Valid AccountDepositRequestDto accountDepositRequestDto, BindingResult bindingResult) {
        AccountDepositResponseDto accountDepositResponseDto = accountService.계좌입금(accountDepositRequestDto);

        return new ResponseEntity<>(new ResponseDto<>(1, "계좌 입금 완료", accountDepositResponseDto), HttpStatus.CREATED);
    }

    @PostMapping("/s/account/withdraw")
    public ResponseEntity<?> withdrawAccount(@RequestBody @Valid AccountWithdrawRequestDto accountWithdrawRequestDto, BindingResult bindingResult, @AuthenticationPrincipal LoginUser loginuser) {
        AccountWithdrawResponseDto accountWithdrawResponseDto = accountService.계좌출금(accountWithdrawRequestDto, loginuser.getUser().getId());

        return new ResponseEntity<>(new ResponseDto<>(1, "계좌 출금 완료", accountWithdrawResponseDto), HttpStatus.CREATED);
    }

    @PostMapping("/s/account/transfer")
    public ResponseEntity<?> withdrawAccount(@RequestBody @Valid AccountTransferRequestDto accountTransferRequestDto, BindingResult bindingResult, @AuthenticationPrincipal LoginUser loginuser) {
        AccountTransferResponseDto accountTransferResponseDto = accountService.계좌이체(accountTransferRequestDto, loginuser.getUser().getId());

        return new ResponseEntity<>(new ResponseDto<>(1, "계좌 이체 완료", accountTransferResponseDto), HttpStatus.CREATED);
    }

    @GetMapping("/s/account/{number}")
    public ResponseEntity<?> findDetailAccount(@PathVariable Long number, @AuthenticationPrincipal LoginUser loginUser, @RequestParam(value = "page", defaultValue = "0") Integer page) {
        AccountDetailResponseDto accountDetailResponseDto = accountService.계좌상세보기(number, loginUser.getUser().getId(), page);

        return new ResponseEntity<>(new ResponseDto<>(1, "계좌 상세보기 성공", accountDetailResponseDto), HttpStatus.OK);
    }
}