package airdnb.be.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
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
        FieldError fieldError = ex.getFieldError();
        return new ErrorResponse(HttpStatus.BAD_REQUEST, getField(fieldError), getMessage(fieldError));
    }

    private String getField(FieldError fieldError) {
        return fieldError.getField();
    }

    private String getMessage(FieldError fieldError) {
        return fieldError.getDefaultMessage();
    }
}
