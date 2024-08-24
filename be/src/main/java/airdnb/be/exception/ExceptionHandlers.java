package airdnb.be.exception;

import airdnb.be.api.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
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
        log.warn("class:{}", ex.getClass(), ex);

        return ErrorResponse.of(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    /**
     * Valid/Validated 로 인한 검증에 의해 필드에 바인딩이 실패 했을 떄
     */
    @ExceptionHandler(BindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<Void> handleBindException(BindException e) {
        String message = e.getAllErrors().get(0).getDefaultMessage();
        log.warn("message : {}", message, e);

        return ApiResponse.badRequest(message);
    }

    /**
     * log level 이 ErrorCode 마다 다르기 때문에 log 는 에러가 발생한 곳에서 작성
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessEx(BusinessException ex) {
        ErrorCode errorCode = ex.getErrorCode();

        return ResponseEntity.status(errorCode.getStatus())
                .body(ErrorResponse.of(errorCode));
    }

    /**
     DefaultHandlerExceptionResolver 에 의해 처리되는 예외도 @ExceptionHandler 로 잡은 이유
     -> 클라이언트에게 어떤 오류로 인해 api 처리가 안됐음을 알려주어 협업 능률이 향상된다고 생각함

     - 기본 에러 응답 메시지
     {
     "timestamp": "2024-08-07T08:08:19.683+00:00",
     "status": 400,
     "error": "Bad Request",
     "path": "/member/exist"
     }
     - MethodArgumentNotValid -> api 처리중, 바디의 값의 검증에서 오류임을 확인
     {
     "code": "0400",
     "status": "BAD_REQUEST",
     "message": "email 필드 오류입니다. 들어온 값 : (*@2@naver.com)"
     }
     */
}
