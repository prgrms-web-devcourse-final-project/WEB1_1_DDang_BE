package team9.ddang.member.service.response;

import team9.ddang.dog.entity.Dog;
import team9.ddang.member.entity.FamilyRole;
import team9.ddang.member.entity.Member;
import team9.ddang.walk.service.response.TimeDuration;
import team9.ddang.walk.util.WalkCalculator;

public record MainResponse(
        Long memberId,
        FamilyRole familyRole,
        String dogName,
        TimeDuration timeDuration,
        int totalDistanceMeter,
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
