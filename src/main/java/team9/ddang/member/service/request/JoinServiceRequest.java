package team9.ddang.member.service.request;

import team9.ddang.global.entity.Gender;
import team9.ddang.member.entity.*;

import java.time.LocalDate;

public record JoinServiceRequest(
        String email,
        Provider provider,
        String name,
        Gender gender,
        String address,
        FamilyRole familyRole,
        String profileImg,
        IsMatched isMatched,
        Role role
) {
    public Member toEntity() {
        return Member.builder()
                .email(email)
                .provider(provider)
                .name(name)
                .gender(gender)
                .address(address)
                .familyRole(familyRole)
                .profileImg(profileImg)
                .isMatched(isMatched)
                .provider(provider)
                .role(role)
                .build();
    }
}
