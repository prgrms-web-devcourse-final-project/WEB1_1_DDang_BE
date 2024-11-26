package team9.ddang.family.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum FamilyExceptionMessage {
    // Family
    FAMILY_NOT_FOUND("해당 가족을 찾을 수 없습니다."),
    MEMBER_ALREADY_IN_FAMILY("해당 멤버는 이미 다른 가족에 속해 있습니다."),
    MEMBER_NOT_IN_FAMILY("해당 멤버는 가족에 속해 있지 않습니다."),
    INVALID_INVITE_CODE("유효하지 않거나 만료된 초대 코드입니다."),

    // Member
    MEMBER_NOT_FOUND("해당 유저를 찾을 수 없습니다."),


    // Dog
    DOG_NOT_FOUND("해당 강아지를 찾을 수 없습니다."),

    // MemberDog
    MEMBER_DOG_NOT_FOUND("강아지가 없으면 패밀리댕을 생성할 수 없습니다.");

    private final String text;
}
