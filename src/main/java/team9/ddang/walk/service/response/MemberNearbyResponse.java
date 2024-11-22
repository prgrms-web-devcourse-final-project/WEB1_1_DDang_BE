package team9.ddang.walk.service.response;

import team9.ddang.global.entity.Gender;

import java.time.LocalDate;

public record MemberNearbyResponse(
        Long dogId,
        String dogProfileImg,
        String dogName,
        String breed,
        int dogWalkCount,
        Long memeberId,
        LocalDate memberBirthDate,
        Gender memberGender
) {
//    public static MemberNearbyResponse of(DogNearbyInfo dog, MemberNearbyInfo member){
//        return new MemberNearbyResponse(dog.profileImg(), dog.dogName(), dog.breed(), dog.walkCount(),
//                ChronoUnit.YEARS.between(member.birthDate(), LocalDate.now()), member.gender());
//    }
}
