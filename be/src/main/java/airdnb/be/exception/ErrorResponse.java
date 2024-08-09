package airdnb.be.exception;

import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;

public record ErrorResponse (
        String code,
        String status,
        String message
){
    public static ErrorResponse of(ErrorCode errorCode) {
        return new ErrorResponse(errorCode.getCode(), getStatusName(errorCode.getStatus()), errorCode.getMessage());
    }

    public static ErrorResponse of(HttpStatus httpStatus, FieldError fieldError) {
        String message = String.format("%s 필드 오류입니다. 들어온 값 : (%s)", fieldError.getField(), fieldError.getRejectedValue());

        return new ErrorResponse(getCode(httpStatus), getStatusName(httpStatus), message);
    }

    public static ErrorResponse of(HttpStatus httpStatus, String message) {
        return new ErrorResponse(getCode(httpStatus), getStatusName(httpStatus), message);
    }

    private static String getCode(HttpStatus httpStatus) {
        return String.format("0%s", httpStatus.value());
    }

    private static String getStatusName(HttpStatus httpStatus) {
        return httpStatus.name();
    }
}
