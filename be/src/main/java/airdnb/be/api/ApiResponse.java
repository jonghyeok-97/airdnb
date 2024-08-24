package airdnb.be.api;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ApiResponse<T> {

    private final int code;
    private final HttpStatus status;
    private final String message;
    private final T data;

    private ApiResponse(HttpStatus status, String message, T data) {
        this.code = status.value();
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
}
