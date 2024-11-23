package team9.ddang.walk.service.response;

import team9.ddang.global.entity.Gender;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public record MemberNearbyResponse(
        Long dogId,
        String dogProfileImg,
        String dogName,
        String breed,
        int dogWalkCount,
        Long memberId,
        long memberAge,
        Gender memberGender
) {
    public static MemberNearbyResponse from(MemberNearbyInfo memberNearbyInfo){
        return new MemberNearbyResponse(memberNearbyInfo.dogId(), memberNearbyInfo.profileImg(), memberNearbyInfo.dogName(),
                memberNearbyInfo.breed(), memberNearbyInfo.walkCount(), memberNearbyInfo.memberId(),
                ChronoUnit.YEARS.between(memberNearbyInfo.birthDate(), LocalDate.now()) + 1, memberNearbyInfo.gender());
    }
}
