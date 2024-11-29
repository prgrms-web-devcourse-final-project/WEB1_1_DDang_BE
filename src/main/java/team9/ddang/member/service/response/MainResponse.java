package team9.ddang.member.service.response;

import team9.ddang.dog.entity.Dog;
import team9.ddang.member.entity.FamilyRole;
import team9.ddang.member.entity.Member;
import team9.ddang.walk.service.response.TimeDuration;
import team9.ddang.walk.util.WalkCalculator;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Main 화면 응답")
public record MainResponse(
        @Schema(description = "산책 나갈 회원 ID", example = "1")
        Long memberId,

        @Schema(description = "산책 나갈 가족 역할")
        FamilyRole familyRole,

        @Schema(description = "강아지 이름", example = "Buddy")
        String dogName,

        @Schema(description = "산책 시간")
        TimeDuration timeDuration,

        @Schema(description = "총 이동 거리 (미터 단위)", example = "1000")
        int totalDistanceMeter,

        @Schema(description = "총 소비 칼로리", example = "50000")
        int totalCalorie
) {
    public static MainResponse of(Member member, Dog dog, long totalSeconds, int totalDistanceMeter){
        return new MainResponse(member.getMemberId(),
                member.getFamilyRole(),
                dog.getName(),
                TimeDuration.from(totalSeconds),
                totalDistanceMeter,
                WalkCalculator.calculateCalorie(dog.getWeight(), totalDistanceMeter));
    }

    public static MainResponse of(Dog dog, long totalSeconds, int totalDistanceMeter){
        return new MainResponse(0L,
                null,
                dog.getName(),
                TimeDuration.from(totalSeconds),
                totalDistanceMeter,
                WalkCalculator.calculateCalorie(dog.getWeight(), totalDistanceMeter));
    }
}

