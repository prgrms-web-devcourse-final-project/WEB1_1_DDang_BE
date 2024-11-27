package team9.ddang.walk.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import team9.ddang.global.exception.ExceptionMessage;

@Getter
@RequiredArgsConstructor
public enum WalkExceptionMessage implements ExceptionMessage {
    //Member
    MEMBER_NOT_FOUND("존재하지 않는 유저 입니다."),
    EMAIL_NOT_FOUND("상대 이메일 정보가 존재하지 않습니다"),


    //Dog
    DOG_NOT_FOUND("존재하지 않는 강아지 입니다."),

    //Walk
    NOT_EXIST_PROPOSAL("제안을 취소했거나 이미 강번따를 진행 중인 유저 입니다."),
    NOT_MATCHED_MEMBER("제안을 한 유저와 받은 유저가 일치하지 않습니다."),
    ALREADY_PROPOSAL("이미 다른 견주분에게 산책을 제안을 하신 상태 입니다."),
    ALREADY_MATCHED_MEMBER("이미 산책 매칭이 된 유저 입니다."),
    ABNORMAL_WALK("산책이 정상적으로 이루어지지 않았습니다."),

    //Token
    TOKEN_NOT_FOUND("토큰을 찾을 수 없습니다."),
    TOKEN_DO_NOT_EXTRACT_EMAIL("AccessToken에서 email을 추출할 수 없습니다.");



    private final String text;
}
