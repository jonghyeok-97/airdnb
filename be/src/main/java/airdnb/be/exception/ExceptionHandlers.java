package airdnb.be.exception;

import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ExceptionHandlers {

    /**
     * 헤더의 미디어타입과 컨트롤러 argument 클래스 타입에 따라 선택된 HttpMessageConverter가 요청 본문을 읽을 수 없을 때
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleHttpMessageNotReadableEx(HttpMessageNotReadableException ex) {
        return ErrorResponse.of(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    /**
     * Valid/Validated 로 인한 검증에 의해 필드에 바인딩이 실패 했을 떄
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

    /**
     * DefaultHandlerExceptionResolver 에 의해 처리되는 예외도 @ExceptionHandler 로 잡은 이유
     *
     * 어떤 오류로 인해 api 처리가 되지 않았는지 알 수 있다고 생각
     *
     * - 기본 에러 응답 메시지
     * {
     *     "timestamp": "2024-08-07T08:08:19.683+00:00",
     *     "status": 400,
     *     "error": "Bad Request",
     *     "path": "/member/exist"
     * }
     * - MethodArgumentNotValid -> api 처리중, 바디의 값의 검증에서 오류임을 확인
     * {
     *     "code": "0400",
     *     "status": "BAD_REQUEST",
     *     "message": "email 필드 오류입니다. 들어온 값 : (*@2@naver.com)"
     * }
     *
     */
}
