package team9.ddang.global.api;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.http.HttpStatus;

@Schema(description = "API 응답 객체")
@Getter
public class ApiResponse<T> {

    @Schema(description = "HTTP 응답 코드", example = "200")
    private int code;

    @Schema(description = "HTTP 상태", example = "OK")
    private HttpStatus status;

    @Schema(description = "응답 메시지", example = "요청이 성공적으로 처리되었습니다.")
    private String message;

    @Schema(description = "응답 데이터")
    private T data;

    public ApiResponse(HttpStatus status, String message, T data) {
        this.code = status.value();
        this.status = status;
        this.message = message;
        this.data = data;
    }

    public static <T> ApiResponse<T> of(HttpStatus httpStatus, String message, T data) {
        return new ApiResponse<>(httpStatus, message, data);
    }

    public static <T> ApiResponse<T> of(HttpStatus httpStatus, T data) {
        return of(httpStatus, httpStatus.name(), data);
    }

    public static <T> ApiResponse<T> ok(T data) {
        return of(HttpStatus.OK, data);
    }

    public static <T> ApiResponse<T> created(T data) {
        return of(HttpStatus.CREATED, data);
    }

    public static ApiResponse<Void> noContent() {
        return of(HttpStatus.NO_CONTENT, "No Content", null);
    }
}
