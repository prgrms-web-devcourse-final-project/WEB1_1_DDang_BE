package team9.ddang.member.service.response;

import io.swagger.v3.oas.annotations.media.Schema;
import team9.ddang.global.entity.Gender;
import team9.ddang.member.entity.FamilyRole;
import team9.ddang.member.entity.Member;

@Schema(description = "회원 정보 수정 응답 데이터")
public record UpdateResponse(

        @Schema(description = "회원 ID", example = "1")
        Long memberId,

        @Schema(description = "회원 이름", example = "John Doe")
        String name,

        @Schema(description = "회원 성별", example = "MALE")
        Gender gender,

        @Schema(description = "가족 내 역할", example = "FATHER")
        FamilyRole familyRole,

        @Schema(description = "회원 프로필 이미지 URL", example = "https://example.com/profile.jpg")
        String profileImg
) {
    public static UpdateResponse from(Member member) {
        return new UpdateResponse(
                member.getMemberId(),
                member.getName(),
                member.getGender(),
                member.getFamilyRole(),
                member.getProfileImg()
        );
    }
}