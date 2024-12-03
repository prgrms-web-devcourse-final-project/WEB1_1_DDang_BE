package team9.ddang.family.service.response;

import io.swagger.v3.oas.annotations.media.Schema;
import team9.ddang.family.entity.Family;

@Schema(description = "가족 기본 정보 응답 데이터")
public record FamilyResponse(
        @Schema(description = "가족 ID", example = "1")
        Long familyId,

        @Schema(description = "가족 대표자 회원 ID", example = "42")
        Long memberId
) {
    public FamilyResponse(Family family) {
        this(
                family.getFamilyId(),
                family.getMember().getMemberId()
        );
    }
}
