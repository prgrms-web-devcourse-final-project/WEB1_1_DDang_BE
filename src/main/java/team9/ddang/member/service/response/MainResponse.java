package team9.ddang.member.service.response;

import io.swagger.v3.oas.annotations.media.Schema;
import team9.ddang.dog.entity.Dog;
import team9.ddang.member.entity.FamilyRole;
import team9.ddang.member.entity.Member;
import team9.ddang.walk.service.response.TimeDuration;
import team9.ddang.walk.util.WalkCalculator;

@Schema(description = "Main 화면 응답")
public record MainResponse(
        @Schema(description = "산책 나갈 회원 ID", example = "1")
        Long memberId,

        @Schema(description = "산책 나갈 가족 역할")
        FamilyRole familyRole,

        @Schema(description = "강아지 이름", example = "Buddy")
        String dogName,

        @Schema(description = "멤버 프로필 이미지", example = "Avatar4.svg")
        String memberProfileImgUrl,

        @Schema(description = "산책 시간")
        TimeDuration timeDuration,

        @Schema(description = "총 이동 거리 (미터 단위)", example = "1000")
        int totalDistanceMeter,

        @Schema(description = "총 소비 칼로리", example = "50")
        int totalCalorie
) {
    public static MainResponse of(Member member, Dog dog, long totalSeconds, int totalDistanceMeter){
        return new MainResponse(member.getMemberId(),
                member.getFamilyRole(),
                dog.getName(),
                member.getProfileImg(),
                team9.ddang.walk.service.response.TimeDuration.from(totalSeconds),
                totalDistanceMeter,
                WalkCalculator.calculateCalorie(dog.getWeight(), totalDistanceMeter));
    }

    public static MainResponse of(Dog dog, long totalSeconds, int totalDistanceMeter){
        return new MainResponse(0L,
                null,
                dog.getName(),
                dog.getProfileImg(),
                team9.ddang.walk.service.response.TimeDuration.from(totalSeconds),
                totalDistanceMeter,
                WalkCalculator.calculateCalorie(dog.getWeight(), totalDistanceMeter));
    }
}

