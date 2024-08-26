package airdnb.be.api;

import airdnb.be.exception.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ApiResponse<T> {

    private final String code;
    private final HttpStatus status;
    private final String message;
    private final T data;

    private ApiResponse(HttpStatus status, String message, T data) {
        this.code = String.format("%04d", status.value());
        this.status = status;
        this.message = message;
        this.data = data;
    }

    private ApiResponse(String code, HttpStatus status, String message, T data) {
        this.code = code;
        this.status = status;
        this.message = message;
        this.data = data;
    }

    public static <T> ApiResponse<T> badRequest(String message) {
        return new ApiResponse<>(HttpStatus.BAD_REQUEST, message, null);
    }

    public static <T> ApiResponse<T> ok(T data) {
        return new ApiResponse<>(HttpStatus.OK, HttpStatus.OK.name(), data);
    }

    public static <T> ApiResponse<T> ok() {
        return new ApiResponse<>(HttpStatus.OK, HttpStatus.OK.name(), null);
    }

    public static <T> ApiResponse<T> errorCode(ErrorCode errorCode) {
        return new ApiResponse<>(errorCode.getCode(), errorCode.getStatus(), errorCode.getMessage(), null);
    }
}
