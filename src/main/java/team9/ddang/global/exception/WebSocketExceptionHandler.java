package team9.ddang.global.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.support.MethodArgumentNotValidException;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import team9.ddang.global.api.WebSocketResponse;

import java.util.stream.Collectors;

@ControllerAdvice
@Slf4j
public class WebSocketExceptionHandler {

    @MessageExceptionHandler(BindException.class)
    @SendToUser("/queue/errors")
    public WebSocketResponse<String> handleBindException(BindException e) {
        log.error("WebSocket BindException: {}", e.getMessage());
        String errorMessage = "유효성 검사 실패: " + e.getBindingResult().getAllErrors().get(0).getDefaultMessage();
        return WebSocketResponse.of(4001, errorMessage, null);
    }

    @MessageExceptionHandler(IllegalArgumentException.class)
    @SendToUser("/queue/errors")
    public WebSocketResponse<String> handleIllegalArgumentException(IllegalArgumentException e) {
        log.error("WebSocket IllegalArgumentException: {}", e.getMessage());
        return WebSocketResponse.of(4002, "잘못된 요청: " + e.getMessage(), null);
    }

    @MessageExceptionHandler(HandlerMethodValidationException.class)
    @SendToUser("/queue/errors")
    public WebSocketResponse<String> handleHandlerMethodValidationException(HandlerMethodValidationException e) {
        String message = e.getAllErrors().stream()
                .map(MessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.joining(", "));

        log.error("WebSocket HandlerMethodValidationException: {}", message);
        return WebSocketResponse.of(4003, "유효성 검사 실패: " + message, null);
    }

    @MessageExceptionHandler(MethodArgumentNotValidException.class)
    @SendToUser("/queue/errors")
    public WebSocketResponse<String> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        assert e.getBindingResult() != null;
        String message = e.getBindingResult().getAllErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.joining(", "));

        log.error("WebSocket MethodArgumentNotValidException: {}", message);
        return WebSocketResponse.of(4004, "유효성 검사 실패: " + message, null);
    }

    @MessageExceptionHandler(Exception.class)
    @SendToUser("/queue/errors")
    public WebSocketResponse<String> handleGeneralException(Exception e) {
        log.error("WebSocket Exception: {}", e.getMessage());
        return WebSocketResponse.of(5000, "서버 내부 오류: " + e.getMessage(), null);
    }
}
