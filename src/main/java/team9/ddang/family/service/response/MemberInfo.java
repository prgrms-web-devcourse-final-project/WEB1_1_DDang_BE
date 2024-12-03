package team9.ddang.family.service.response;

import io.swagger.v3.oas.annotations.media.Schema;
import team9.ddang.global.entity.Gender;
import team9.ddang.member.entity.FamilyRole;
import team9.ddang.member.entity.Member;

import java.util.List;

public record MemberInfo(

        @Schema(description = "회원 ID", example = "42")
        Long memberId,

        @Schema(description = "회원 이메일", example = "user@example.com")
        String email,

        @Schema(description = "회원 이름", example = "John Doe")
        String name,

        @Schema(description = "회원 성별 (MALE/FEMALE)", example = "MALE")
        Gender gender,

        @Schema(description = "가족 내 역할", example = "PARENT")
        FamilyRole familyRole,

        @Schema(description = "회원 프로필 이미지 URL", example = "profile.jpg")
        String profileImg,

        @Schema(description = "회원의 산책 일정 목록")
        List<WalkScheduleInfo> walkScheduleInfoList
) {
    public MemberInfo(Member member, List<WalkScheduleInfo> walkScheduleInfoList) {
        this(
                member.getMemberId(),
                member.getEmail(),
                member.getName(),
                member.getGender(),
                member.getFamilyRole(),
                member.getProfileImg(),
                walkScheduleInfoList
        );
    }
}
