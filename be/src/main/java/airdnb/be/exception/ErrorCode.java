package airdnb.be.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // 회원
    ALREADY_EXISTS_MEMBER("1000", HttpStatus.BAD_REQUEST, "이미 존재하는 회원입니다."),

    // 인증
    AUTH_MISMATCH("1100", HttpStatus.BAD_REQUEST, "인증 되지 않았습니다"),
    ;

    private final String code;
    private final HttpStatus status;
    private final String message;
}
