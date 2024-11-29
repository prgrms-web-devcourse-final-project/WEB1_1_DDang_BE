package team9.ddang.dog.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum DogExceptionMessage {

    // Member
    MEMBER_NOT_FOUND("해당 유저를 찾을 수 없습니다."),
    ONLY_FAMILY_OWNER_CREATE("패밀리댕 주인만 강아지를 추가할 수 있습니다."),

    // Dog
    DOG_NOT_FOUND("해당 강아지를 찾을 수 없습니다."),
    DOG_ONLY_ONE("강아지는 한마리만 소유할 수 있습니다.");


    private final String text;
}
