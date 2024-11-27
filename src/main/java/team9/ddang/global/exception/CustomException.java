package team9.ddang.global.exception;

public abstract class CustomException extends RuntimeException {

    public CustomException(ExceptionMessage message) {
        super(message.getText());
    }
}
