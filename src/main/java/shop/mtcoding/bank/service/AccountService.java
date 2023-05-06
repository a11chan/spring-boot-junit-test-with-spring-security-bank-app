package shop.mtcoding.bank.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.mtcoding.bank.domain.account.Account;
import shop.mtcoding.bank.domain.account.AccountRepository;
import shop.mtcoding.bank.domain.user.User;
import shop.mtcoding.bank.domain.user.UserRepository;
import shop.mtcoding.bank.dto.account.AccountSaveRequestDto;
import shop.mtcoding.bank.dto.account.AccountSaveResponseDto;
import shop.mtcoding.bank.handler.ex.CustomApiException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class AccountService {

    private final UserRepository userRepository;
    private final AccountRepository accountRepository;

    public AccountListResponseDto 계좌목록보기_유저별(Long userId) {
        User userPS = userRepository.findById(userId).orElseThrow(() -> new CustomApiException("유저를 찾을 수 없습니다."));

        // User의 모든 계좌 목록
        List<Account> accountListPS = accountRepository.findByUser_id(userId);

        return new AccountListResponseDto(userPS, accountListPS);
    }

    @Getter
    public static class AccountListResponseDto {

        private final String fullname;
        private final List<AccountDto> accounts;

        public AccountListResponseDto(final User user, final List<Account> accounts) {
            this.fullname = user.getFullname();
            this.accounts = accounts.stream().map(AccountDto::new).collect(Collectors.toList());
        }

        @Getter
        public static class AccountDto {

            private final Long id;
            private final Long number;
            private final Long balance;

            public AccountDto(final Account account) {
                this.id = account.getId();
                this.number = account.getNumber();
                this.balance = account.getBalance();
            }
        }
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

}
