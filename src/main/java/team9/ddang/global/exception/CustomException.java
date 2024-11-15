package team9.ddang.global.exception;

public abstract class CustomException extends RuntimeException {

    public CustomException(String message) {
        super(message);
    }
}
