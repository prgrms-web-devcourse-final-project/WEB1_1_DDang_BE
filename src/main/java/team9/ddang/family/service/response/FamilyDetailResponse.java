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

        @Schema(description = "가족 구성원 목록")
        List<MemberInfo> members,

        @Schema(description = "가족의 강아지 목록")
        List<GetDogResponse> dogs,

        @Schema(description = "강아지의 총 산책 횟수", example = "10")
        int totalWalkCount,

        @Schema(description = "강아지의 총 산책 거리 (킬로미터)", example = "5000")
        double totalDistanceInKilometers,

        @Schema(description = "강아지의 총 소요 칼로리", example = "1200")
        int totalCalorie
) {
    public FamilyDetailResponse(Family family, List<MemberInfo> members, List<GetDogResponse> dogs,
                                int totalWalkCount, double totalDistanceInKilometers, int totalCalorie) {
        this(
                family.getFamilyId(),
                family.getMember().getMemberId(),
                members,
                dogs,
                totalWalkCount,
                totalDistanceInKilometers,
                totalCalorie
        );
    }
}
