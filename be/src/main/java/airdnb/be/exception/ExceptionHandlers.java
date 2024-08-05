package airdnb.be.exception;

import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ExceptionHandlers {

    /**
     * @Valid 로 인해 필드에 바인딩이 실패 했을 떄
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleMethodArgumentNotValidEx(MethodArgumentNotValidException ex) {
        FieldError fieldError = Objects.requireNonNull(ex.getFieldError());
        log.error("[클래스명]{}  [필드]{}  [거부값]{}", ex.getClass(), fieldError.getField(), fieldError.getRejectedValue());

        return ErrorResponse.of(HttpStatus.BAD_REQUEST, fieldError);
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessEx(BusinessException ex) {
        ErrorCode errorCode = ex.getErrorCode();
        log.error("[{}]", errorCode);

        return ResponseEntity.status(errorCode.getStatus())
                .body(ErrorResponse.of(errorCode));
    }
}
