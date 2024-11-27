package team9.ddang.family.service.response;

import io.swagger.v3.oas.annotations.media.Schema;
import team9.ddang.family.entity.Family;

@Schema(description = "가족 초대 코드 응답 데이터")
public record InviteCodeResponse(
        @Schema(description = "가족 ID", example = "1")
        Long familyId,

        @Schema(description = "초대 코드", example = "ABC12345")
        String inviteCode,

        @Schema(description = "초대 코드 만료까지 남은 시간(초)", example = "300")
        long expiresInSeconds
) {
    public InviteCodeResponse(Family family, String inviteCode, long expiresInSeconds) {
        this(
                family.getFamilyId(),
                inviteCode,
                expiresInSeconds
        );
    }
}