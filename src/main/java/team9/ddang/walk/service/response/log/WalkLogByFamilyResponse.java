package team9.ddang.walk.service.response.log;

import team9.ddang.member.entity.FamilyRole;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "가족 구성원의 산책 기록 응답")
public record WalkLogByFamilyResponse(
        @Schema(description = "멤버 ID", example = "1")
        Long memberId,
        @Schema(description = "가족 역할", example = "FATHER")
        FamilyRole familyRole,
        @Schema(description = "산책 횟수", example = "10")
        int count
) {
}
