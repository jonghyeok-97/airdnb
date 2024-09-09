package airdnb.be.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // 회원
    ALREADY_EXISTS_MEMBER("1000", HttpStatus.BAD_REQUEST, "이미 존재하는 회원입니다."),
    CANNOT_LOGIN("1001", HttpStatus.BAD_REQUEST, "아이디나 비밀번호가 틀렸습니다"),
    NOT_EXIST_MEMBER("1002", HttpStatus.BAD_REQUEST, "존재하지 않는 회원입니다"),

    // 인증
    AUTH_MISMATCH("1100", HttpStatus.BAD_REQUEST, "인증 되지 않았습니다"),

    // 숙소
    NOT_EXIST_STAY("1200", HttpStatus.BAD_REQUEST, "존재하지 않는 숙소입니다."),
    CANNOT_CAPABLE_GUEST("1201", HttpStatus.BAD_REQUEST, "수용할 수 없는 인원입니다."),

    // 외부 API
    MAIL_SERVER_ERROR("2000", HttpStatus.INTERNAL_SERVER_ERROR, "내부 오류입니다. 이용에 불편을 드려서 죄송합니다");

    private final String code;
    private final HttpStatus status;
    private final String message;
}
