package team9.ddang.walk.service.response.walk;

import team9.ddang.global.entity.Gender;
import team9.ddang.member.entity.IsMatched;

import java.time.LocalDate;

public record MemberNearbyInfo(
        Long dogId,
        String breed,
        String dogName,
        String profileImg,
        int walkCount,
        Long memberId,
        LocalDate dogBirthDate,
        Gender dogGender,
        IsMatched isMatched,
        String email
) { }
