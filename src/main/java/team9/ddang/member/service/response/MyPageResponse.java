package team9.ddang.member.service.response;

import io.swagger.v3.oas.annotations.media.Schema;
import team9.ddang.dog.service.response.GetDogResponse;
import team9.ddang.global.entity.Gender;
import team9.ddang.member.entity.FamilyRole;
import team9.ddang.member.entity.IsMatched;
import team9.ddang.member.entity.Member;

@Schema(description = "마이페이지 응답 데이터")
public record MyPageResponse(
        @Schema(description = "회원 ID", example = "1")
        Long memberId,

        @Schema(description = "회원 이름", example = "John Doe")
        String name,

        @Schema(description = "회원 주소", example = "123 Main Street")
        String address,

        @Schema(description = "회원 성별", example = "MALE")
        Gender gender,

        @Schema(description = "가족 내 역할", example = "FATHER")
        FamilyRole familyRole,

        @Schema(description = "회원 프로필 이미지 URL", example = "https://example.com/profile.jpg")
        String profileImg,

        @Schema(description = "총 산책 거리 (킬로미터)", example = "12.5")
        double totalDistance,

        @Schema(description = "총 산책 횟수", example = "5")
        int walkCount,

        @Schema(description = "강번따 횟수", example = "3")
        int countWalksWithMember,

        @Schema(description = "강아지 정보")
        GetDogResponse dog

) {
    public static MyPageResponse from(Member member, double totalDistance, int walkCount, int countWalksWithMember, GetDogResponse dog) {
        return new MyPageResponse(
                member.getMemberId(),
                member.getName(),
                member.getAddress(),
                member.getGender(),
                member.getFamilyRole(),
                member.getProfileImg(),
                totalDistance,
                walkCount,
                countWalksWithMember,
                dog
        );
    }
}
