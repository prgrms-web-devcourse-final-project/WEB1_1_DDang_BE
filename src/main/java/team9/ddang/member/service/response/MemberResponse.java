package team9.ddang.member.service.response;

import jakarta.persistence.*;
import team9.ddang.family.entity.Family;
import team9.ddang.global.entity.Gender;
import team9.ddang.member.entity.FamilyRole;
import team9.ddang.member.entity.IsMatched;
import team9.ddang.member.entity.Member;

import java.time.LocalDate;

public record MemberResponse(
        Long memberId,
        String name,
        String email,
        LocalDate birthDate,
        Gender gender,
        FamilyRole familyRole,
        String profileImg,
        Family family
) {
    public static MemberResponse from(Member member) {
        return new MemberResponse(
                member.getMemberId(),
                member.getName(),
                member.getEmail(),
                member.getBirthDate(),
                member.getGender(),
                member.getFamilyRole(),
                member.getProfileImg(),
                member.getFamily());
    }
}
