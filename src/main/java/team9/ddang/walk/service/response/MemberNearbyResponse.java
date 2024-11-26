package team9.ddang.walk.service.response;

import io.swagger.v3.oas.annotations.media.Schema;
import team9.ddang.global.entity.Gender;

import static team9.ddang.walk.util.DateCalculator.calculateAgeFromNow;

@Schema(description = "근처 회원 정보 응답 객체")
public record MemberNearbyResponse(
        @Schema(description = "강아지의 식별자", example = "4")
        Long dogId,

        @Schema(description = "강아지 프로필 이미지 URL", example = "https://example.com/dog/profile.jpg")
        String dogProfileImg,

        @Schema(description = "강아지 이름", example = "초코")
        String dogName,

        @Schema(description = "강아지 품종", example = "말티즈")
        String breed,

        @Schema(description = "강아지와의 산책 횟수", example = "10")
        int dogWalkCount,

        @Schema(description = "강아지 나이", example = "5")
        long dogAge,

        @Schema(description = "강아지 성별", example = "FEMALE")
        Gender dogGender,

        @Schema(description = "회원의 고유 식별자", example = "5")
        Long memberId,

        @Schema(description = "회원 이메일", example = "example@example.com")
        String memberEmail
) {
    public static MemberNearbyResponse from(MemberNearbyInfo memberNearbyInfo){
        return new MemberNearbyResponse(memberNearbyInfo.dogId(), memberNearbyInfo.profileImg(), memberNearbyInfo.dogName(),
                memberNearbyInfo.breed(), memberNearbyInfo.walkCount(), calculateAgeFromNow(memberNearbyInfo.dogBirthDate()),
                memberNearbyInfo.dogGender(),memberNearbyInfo.memberId(), memberNearbyInfo.email());
    }
}
