package shop.mtcoding.bank.service;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
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
import shop.mtcoding.bank.dto.account.AccountListResponseDto;
import shop.mtcoding.bank.dto.account.AccountSaveRequestDto;
import shop.mtcoding.bank.dto.account.AccountSaveResponseDto;
import shop.mtcoding.bank.handler.ex.CustomApiException;
import shop.mtcoding.bank.util.CustomDateUtil;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
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
                .withdrawAccount(null)
                .depositAccount(depositAccountPS)
                .depositAccountBalance(depositAccountPS.getBalance())
                .withdrawAccountBalance(null)
                .amount(accountDepositRequestDto.getAmount())
                .gubun(TransactionEnum.DEPOSIT)
                .sender("ATM")
                .receiver(accountDepositRequestDto.getNumber() + "")
                .tel(accountDepositRequestDto.getTel())
                .build();

        Transaction txPS = transactionRepository.save(tx);
        return new AccountDepositResponseDto(depositAccountPS, txPS);
    }

    @Getter
    public static class AccountDepositResponseDto {

        private final Long id; // 계좌 ID
        private final Long number; // 계좌번호
        private final TransactionDto transactionDto; // 거래내역

        public AccountDepositResponseDto(final Account account, final Transaction transaction) {
            this.id = account.getId();
            this.number = account.getNumber();
            this.transactionDto = new TransactionDto(transaction);
        }

        @Getter
        public class TransactionDto {

            private final Long id;
            private final String gubun;
            private final String sender;
            private final String receiver;
            private final Long amount;

            @JsonIgnore // 클라이언트에게 전달 안 함, 서비스 테스트 용
            private final Long depositAccountBalance;
            private final String tel;
            private final String createdAt;

            public TransactionDto(final Transaction transaction) {
                this.id = transaction.getId();
                this.gubun = transaction.getGubun().getValue();
                this.sender = transaction.getSender();
                this.receiver = transaction.getReceiver();
                this.amount = transaction.getAmount();
                this.depositAccountBalance = transaction.getDepositAccountBalance();
                this.tel = transaction.getTel();
                this.createdAt = CustomDateUtil.toStringFormat(transaction.getCreatedAt());
            }
        }
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AccountDepositRequestDto {
        @NotNull
        @Digits(integer = 4, fraction = 4)
        private Long number;

        @NotNull
        private Long amount;

        @NotEmpty
        @Pattern(regexp = "^(DEPOSIT)$")
        private String gubun;

        @NotEmpty
        @Pattern(regexp = "^[0-9]{11}")
        private String tel;
    }
}