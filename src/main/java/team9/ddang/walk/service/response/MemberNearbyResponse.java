package team9.ddang.walk.service.response;

public record MemberNearbyResponse(
        String dogProfileImg,
        String dogName,
        String breed,
        int dogWalkCount,
        int personAge,
        int personGender
) {
}
