package team9.ddang.family.service.response;

import io.swagger.v3.oas.annotations.media.Schema;
import team9.ddang.dog.service.response.GetDogResponse;
import team9.ddang.family.entity.Family;
import team9.ddang.member.entity.Member;
import team9.ddang.member.service.response.MemberResponse;

import java.util.List;

@Schema(description = "가족 상세 정보 응답 데이터")
public record FamilyDetailResponse(
        @Schema(description = "가족 ID", example = "1")
        Long familyId,

        @Schema(description = "가족 대표자 회원 ID", example = "42")
        Long memberId,

        @Schema(description = "가족 이름", example = "행복한 가족")
        String familyName,

        @Schema(description = "가족 구성원 목록")
        List<MemberResponse> members,

        @Schema(description = "가족의 강아지 목록")
        List<GetDogResponse> dogs
) {
    public FamilyDetailResponse(Family family, List<MemberResponse> members, List<GetDogResponse> dogs) {
        this(
                family.getFamilyId(),
                family.getMember().getMemberId(),
                family.getFamilyName(),
                members,
                dogs
        );
    }
}
