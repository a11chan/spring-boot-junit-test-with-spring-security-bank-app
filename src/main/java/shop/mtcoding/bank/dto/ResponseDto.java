package shop.mtcoding.bank.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ResponseDto<T> {

    // 현재 DTO는 한 번 생성 후 수정되지 않으므로 final 선언
    private final Integer code; // 1 성공, -1 실패
    private final String msg;
    private final T data;
}
