package airdnb.be.exception;

import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;

public record ErrorResponse (
        String code,
        String status,
        String message
){
    public static ErrorResponse of(ErrorCode errorCode) {
        return new ErrorResponse(errorCode.getCode(), errorCode.getStatus().name(), errorCode.getMessage());
    }

    public static ErrorResponse of(HttpStatus httpStatus, FieldError fieldError) {
        String code = String.format("0%s", httpStatus.value());
        String status = httpStatus.name();
        String message = String.format("%s 검증 실패입니다. (%s)", fieldError.getField(), fieldError.getRejectedValue());

        return new ErrorResponse(code, status, message);
    }
}
