package team9.ddang.member.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MemberExceptionMessage {

    // Member
    MEMBER_NOT_FOUND("해당 유저를 찾을 수 없습니다."),
    MEMBER_NOT_REPORT_SELF("본인을 신고할 수 없습니다.");

    private final String text;
}
