package team9.ddang.member.service.response;

import team9.ddang.global.entity.Gender;
import team9.ddang.member.entity.FamilyRole;
import team9.ddang.member.entity.Member;
import team9.ddang.member.entity.Provider;

import java.time.LocalDate;

public record MemberResponse(
        Long memberId,
        String name,
        String email,
        Provider provider,
        LocalDate birthDate,
        Gender gender,
        String address,
        FamilyRole familyRole,
        String profileImg
) {
    public static MemberResponse from(Member member) {
        return new MemberResponse(
                member.getMemberId(),
                member.getName(),
                member.getEmail(),
                member.getProvider(),
                member.getBirthDate(),
                member.getGender(),
                member.getAddress(),
                member.getFamilyRole(),
                member.getProfileImg());
    }
}
