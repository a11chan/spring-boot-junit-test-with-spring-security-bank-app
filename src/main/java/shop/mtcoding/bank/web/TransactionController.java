package shop.mtcoding.bank.web;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import shop.mtcoding.bank.config.auth.LoginUser;
import shop.mtcoding.bank.dto.ResponseDto;
import shop.mtcoding.bank.dto.transaction.TransactionListResponseDto;
import shop.mtcoding.bank.service.TransactionService;

@RequestMapping(value = "/api")
@RequiredArgsConstructor
@RestController
public class TransactionController {

    private final TransactionService transactionService;

    @GetMapping("/s/account/{number}/transaction")
    public ResponseEntity<?> findTransactionList(
            @PathVariable Long number,
            @RequestParam(value = "gubun", defaultValue = "ALL") String gubun,
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @AuthenticationPrincipal LoginUser loginUser) {
        TransactionListResponseDto transactions = transactionService.입출금목록보기(loginUser.getUser().getId(), number, gubun, page);

//        return new ResponseEntity<>(new ResponseDto<>(1, "입출금 목록 보기 성공", transactions), HttpStatus.OK);
        return ResponseEntity.ok().body(new ResponseDto<>(1, "입출금 목록 보기 성공", transactions));
    }
}