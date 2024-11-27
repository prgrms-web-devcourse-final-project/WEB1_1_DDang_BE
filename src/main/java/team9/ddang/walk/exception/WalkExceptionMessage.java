package team9.ddang.walk.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import team9.ddang.global.exception.ExceptionMessage;

@Getter
@RequiredArgsConstructor
public enum WalkExceptionMessage implements ExceptionMessage {
    //Member



    //Token
    TOKEN_NOT_FOUND("토큰을 찾을 수 없습니다."),
    TOKEN_DO_NOT_EXTRACT_EMAIL("AccessToken에서 email을 추출할 수 없습니다.");



    private final String text;
}
