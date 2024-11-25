package team9.ddang.walk.service.response;

import io.swagger.v3.oas.annotations.media.Schema;
import team9.ddang.global.entity.Gender;

import java.time.temporal.ChronoUnit;

public record MemberNearbyResponse(
        @Schema(description = "강아지의 식별자", example = "4")
        Long dogId,

        @Schema(description = "강아지 프로필 이미지 URL", example = "https://example.com/dog/profile.jpg")
        String dogProfileImg,

        @Schema(description = "강아지 이름", example = "초코")
        String dogName,
        String breed,

        @Schema(description = "강아지와의 산책 횟수", example = "10")
        int dogWalkCount,

        @Schema(description = "회원의 고유 식별자", example = "5")
        Long memberId,

        @Schema(description = "회원 나이", example = "30")
        long memberAge,

        @Schema(description = "회원 성별", example = "FEMALE")
        Gender memberGender
) {
    public static MemberNearbyResponse from(MemberNearbyInfo memberNearbyInfo){
        return new MemberNearbyResponse(memberNearbyInfo.dogId(), memberNearbyInfo.profileImg(), memberNearbyInfo.dogName(),
                memberNearbyInfo.breed(), memberNearbyInfo.walkCount(), memberNearbyInfo.memberId(),
                ChronoUnit.YEARS.between(memberNearbyInfo.birthDate(), LocalDate.now()) + 1, memberNearbyInfo.gender());
    }
}
