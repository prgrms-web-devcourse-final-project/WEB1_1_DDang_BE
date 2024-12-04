package team9.ddang.member.service.request;

import team9.ddang.global.entity.Gender;
import team9.ddang.member.entity.FamilyRole;
import team9.ddang.member.entity.Member;

public record UpdateServiceRequest(
        String name,
        Gender gender,
        FamilyRole familyRole,
        String profileImg
) {
    public Member toEntity(Member member) {
        member.updateName(name);
        member.updateGender(gender);
        member.updateFamilyRole(familyRole);
        member.updateProfileImg(profileImg);
        return member;
    }
}
