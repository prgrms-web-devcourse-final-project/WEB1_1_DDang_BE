package team9.ddang.member.service.request;

import team9.ddang.family.entity.Family;
import team9.ddang.global.entity.Gender;
import team9.ddang.member.entity.*;

import java.time.LocalDate;

public record JoinServiceRequest(
        String email,
        String name,
        LocalDate birthDate,
        Gender gender,
        FamilyRole familyRole,
        Family family,
        String profileImg,
        IsMatched isMatched,
        Provider provider,
        Role role
) {
    public Member toEntity() {
        return Member.builder()
                .email(email)
                .name(name)
                .birthDate(birthDate)
                .gender(gender)
                .familyRole(familyRole)
                .family(family)
                .profileImg(profileImg)
                .isMatched(isMatched)
                .provider(provider)
                .role(role)
                .build();
    }
}
