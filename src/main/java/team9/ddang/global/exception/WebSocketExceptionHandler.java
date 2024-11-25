package team9.ddang.global.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.handler.annotation.support.MethodArgumentNotValidException;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

import java.util.stream.Collectors;

@ControllerAdvice
@Slf4j
public class WebSocketExceptionHandler {
    @MessageExceptionHandler(BindException.class)
    @SendToUser("/queue/errors")
    public String handleBindException(BindException e) {
        log.error("WebSocket BindException: {}", e.getMessage());
        return "유효성 검사 실패: " + e.getBindingResult().getAllErrors().get(0).getDefaultMessage();
    }

    @MessageExceptionHandler(IllegalArgumentException.class)
    @SendTo("/queue/errors")
    public String handleIllegalArgumentException(IllegalArgumentException e) {
        log.error("WebSocket IllegalArgumentException: {}", e.getMessage());
        return "잘못된 요청: " + e.getMessage();
    }

    @MessageExceptionHandler(HandlerMethodValidationException.class)
    @SendToUser("/queue/errors")
    public String handleHandlerMethodValidationException(HandlerMethodValidationException e) {
        String message = e.getAllErrors().stream()
                .map(error -> error.getDefaultMessage())
                .collect(Collectors.joining(", "));

        log.error("WebSocket HandlerMethodValidationException: {}", message);
        return "유효성 검사 실패: " + message;
    }

    @MessageExceptionHandler(MethodArgumentNotValidException.class)
    @SendTo("/queue/errors")
    public String handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getAllErrors().stream()
                .map(error -> error.getDefaultMessage())
                .collect(Collectors.joining(", "));

        log.error("WebSocket MethodArgumentNotValidException: {}", message);
        return "유효성 검사 실패: " + message;
    }
}
