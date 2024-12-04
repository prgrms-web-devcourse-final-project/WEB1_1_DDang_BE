package team9.ddang.chat.service.response;

import team9.ddang.global.entity.Gender;
import team9.ddang.member.entity.FamilyRole;
import team9.ddang.member.entity.Member;

public record ChatMemberInfo(
        Long memberId,
        String email,
        String name,
        Gender gender,
        FamilyRole familyRole,
        String profileImg
        ) {

    public ChatMemberInfo(Member member) {
        this(
                member.getMemberId(),
                member.getEmail(),
                member.getName(),
                member.getGender(),
                member.getFamilyRole(),
                member.getProfileImg()
        );
    }
}
