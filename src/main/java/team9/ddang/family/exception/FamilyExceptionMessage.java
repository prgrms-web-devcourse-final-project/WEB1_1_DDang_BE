package team9.ddang.family.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum FamilyExceptionMessage {
    // Family
    FAMILY_NOT_FOUND("해당 가족을 찾을 수 없습니다.");


    private final String text;
}
