package team9.ddang.global.exception;

public class AuthenticationException extends CustomException{

    public AuthenticationException(ExceptionMessage exceptionMessage) {
        super(exceptionMessage);
    }
}
