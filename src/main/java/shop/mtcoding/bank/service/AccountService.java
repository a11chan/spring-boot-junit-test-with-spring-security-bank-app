package shop.mtcoding.bank.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.mtcoding.bank.domain.account.Account;
import shop.mtcoding.bank.domain.account.AccountRepository;
import shop.mtcoding.bank.domain.transaction.Transaction;
import shop.mtcoding.bank.domain.transaction.TransactionEnum;
import shop.mtcoding.bank.domain.transaction.TransactionRepository;
import shop.mtcoding.bank.domain.user.User;
import shop.mtcoding.bank.domain.user.UserRepository;
import shop.mtcoding.bank.dto.account.AccountRequestDto.AccountDepositRequestDto;
import shop.mtcoding.bank.dto.account.AccountRequestDto.AccountSaveRequestDto;
import shop.mtcoding.bank.dto.account.AccountRequestDto.AccountTransferRequestDto;
import shop.mtcoding.bank.dto.account.AccountRequestDto.AccountWithdrawRequestDto;
import shop.mtcoding.bank.dto.account.AccountResponseDto.*;
import shop.mtcoding.bank.handler.ex.CustomApiException;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class AccountService {

    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    public AccountListResponseDto 계좌목록보기_유저별(Long userId) {
        User userPS = userRepository.findById(userId).orElseThrow(() -> new CustomApiException("유저를 찾을 수 없습니다."));

        // User의 모든 계좌 목록
        List<Account> accountListPS = accountRepository.findByUser_id(userId);

        return new AccountListResponseDto(userPS, accountListPS);
    }

    @Transactional
    public AccountSaveResponseDto 계좌등록(AccountSaveRequestDto accountSaveRequestDto, Long userId) {
        // User 로그인되어 있는 상태는 컨트롤러에서 검증
        // User가 DB에 있는지 검증
        User userPS = userRepository.findById(userId).orElseThrow(() -> new CustomApiException("유저를 찾을 수 없습니다."));

        // 해당 계좌가 DB에 있는지 중복 여부를 체크
        Optional<Account> accountOP = accountRepository.findByNumber(accountSaveRequestDto.getNumber());
        if (accountOP.isPresent()) throw new CustomApiException("해당 계좌가 이미 존재합니다.");

        // 계좌 등록
        Account accountPS = accountRepository.save(accountSaveRequestDto.toEntity(userPS));

        // 결과 DTO로 응답
        return new AccountSaveResponseDto(accountPS);
    }

    @Transactional
    public void 계좌삭제(Long number, Long userId) {

        // 1. 계좌 확인
        Account accountPS = accountRepository.findByNumber(number).orElseThrow(
                () -> new CustomApiException("계좌를 찾을 수 없습니다.")
        );
        // 2. 계좌 소유자 확인
        accountPS.checkOwner(userId);

        // 3. 계좌 삭제
        accountRepository.deleteById(accountPS.getId());
    }

    //인증 불요
    @Transactional
    public AccountDepositResponseDto 계좌입금(AccountDepositRequestDto accountDepositRequestDto) {
        //0원 입금 차단
        if (accountDepositRequestDto.getAmount() <= 0L) {
            throw new CustomApiException("0원 이하의 금액을 입금할 수 없습니다.");
        }

        //입금 계좌 존재 여부 확인
        Account depositAccountPS = accountRepository.findByNumber(accountDepositRequestDto.getNumber()).orElseThrow(() -> new CustomApiException("계좌를 찾을 수 없습니다."));

        //입금 - 해당 계좌 잔액 update -> dirty checking
        depositAccountPS.deposit(accountDepositRequestDto.getAmount());

        //거래내역 기록
        Transaction tx = Transaction.builder()
                .depositAccount(depositAccountPS)
                .withdrawAccount(null)
                .withdrawAccountBalance(null)
                .depositAccountBalance(depositAccountPS.getBalance())
                .txAmount(accountDepositRequestDto.getAmount())
                .gubun(TransactionEnum.DEPOSIT)
                .sender("ATM")
                .receiver(accountDepositRequestDto.getNumber() + "")
                .tel(accountDepositRequestDto.getTel())
                .build();

        Transaction txPS = transactionRepository.save(tx);

        return new AccountDepositResponseDto(depositAccountPS, txPS);
    }

    @Transactional
    public AccountWithdrawResponseDto 계좌출금(AccountWithdrawRequestDto accountWithdrawRequestDto, Long userId) {
        // 0원 확인
        if (accountWithdrawRequestDto.getAmount() <= 0L) throw new CustomApiException("0원 이하의 금액을 입금할 수 없습니다.");
        // 계좌 존재 확인
        Account withdrawAccountPS = accountRepository.findByNumber(accountWithdrawRequestDto.getNumber()).orElseThrow(() -> new CustomApiException("계좌를 찾을 수 없습니다."));
        // 출금 주체 확인(로그인한 사람과 예금주 일치 여부)
        withdrawAccountPS.checkOwner(userId);
        // 출금 계좌 비밀번호 확인
        withdrawAccountPS.checkPassword(accountWithdrawRequestDto.getPassword());
        // 출금 계좌 잔액 확인
        withdrawAccountPS.checkBalance(accountWithdrawRequestDto.getAmount());
        // 출금하기
        withdrawAccountPS.withdraw(accountWithdrawRequestDto.getAmount());
        // 거래내역 남기기
        Transaction tx = Transaction.builder()
                .depositAccount(null)
                .withdrawAccount(withdrawAccountPS)
                .depositAccountBalance(null)
                .withdrawAccountBalance(withdrawAccountPS.getBalance())
                .txAmount(accountWithdrawRequestDto.getAmount())
                .gubun(TransactionEnum.WITHDRAW)
                .sender(accountWithdrawRequestDto.getNumber() + "")
                .receiver("ATM")
                .tel(null)
                .build();
        Transaction txPS = transactionRepository.save(tx);

        // DTO 응답
        return new AccountWithdrawResponseDto(withdrawAccountPS, txPS);
    }

    @Transactional
    public AccountTransferResponseDto 계좌이체(AccountTransferRequestDto accountTransferRequestDto, Long userId) {
        // 출금 계좌와 입금 계좌가 다른지 확인
        if (accountTransferRequestDto.getWithdrawNumber().equals(accountTransferRequestDto.getDepositNumber()))
            throw new CustomApiException("동일한 계좌 간 이체는 불가능합니다.");
        // 0원 체크
        if (accountTransferRequestDto.getTxAmount() <= 0) throw new CustomApiException("0원 이하의 이체는 불가능합니다.");

        // 출금 계좌 확인
        Account withdrawAccountPS = accountRepository.findByNumber(accountTransferRequestDto.getWithdrawNumber()).orElseThrow(() -> new CustomApiException("출금할 계좌를 찾을 수 없습니다."));
        // 입금 계좌 확인
        Account depositAccountPS = accountRepository.findByNumber(accountTransferRequestDto.getDepositNumber()).orElseThrow(() -> new CustomApiException("입금할 계좌를 찾을 수 없습니다."));

        // 출금 계좌와 로그인 사용자 일치 확인
        withdrawAccountPS.checkOwner(userId);
        // 출금 계좌 비밀번호 일치 확인
        withdrawAccountPS.checkPassword(accountTransferRequestDto.getWithdrawPassword());
        // 출금 계좌 잔액 확인
        withdrawAccountPS.checkBalance(accountTransferRequestDto.getTxAmount());

        // 이체하기
        withdrawAccountPS.withdraw(accountTransferRequestDto.getTxAmount());
        depositAccountPS.deposit(accountTransferRequestDto.getTxAmount());

        // 거래내역 남기기
        Transaction tx = Transaction.builder()
                .depositAccount(depositAccountPS)
                .withdrawAccount(withdrawAccountPS)
                .depositAccountBalance(depositAccountPS.getBalance())
                .withdrawAccountBalance(withdrawAccountPS.getBalance())
                .txAmount(accountTransferRequestDto.getTxAmount())
                .gubun(TransactionEnum.TRANSFER)
                .sender(accountTransferRequestDto.getWithdrawNumber() + "")
                .receiver(accountTransferRequestDto.getDepositNumber() + "")
                .build();
        Transaction txPS = transactionRepository.save(tx);

        return new AccountTransferResponseDto(withdrawAccountPS, txPS);
    }

    public AccountDetailResponseDto 계좌상세보기(Long number, Long userId, Integer page) {
        // 구분값, 페이지 고정
        String gubun = "ALL";
        Account accountPS = accountRepository.findByNumber(number).orElseThrow(() -> new CustomApiException("계좌를 찾을 수 없습니다."));
        accountPS.checkOwner(userId);

        List<Transaction> transactionList = transactionRepository.findTransactionList(accountPS.getId(), gubun, page);

        return new AccountDetailResponseDto(accountPS, transactionList);
    }
}