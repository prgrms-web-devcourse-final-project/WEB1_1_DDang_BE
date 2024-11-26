package team9.ddang.chat.service.response;

import team9.ddang.member.entity.Member;

public record ChatMemberInfo(
        Long memberId,
        String email,
        String name) {

    public ChatMemberInfo(Member member) {
        this(
                member.getMemberId(),
                member.getEmail(),
                member.getName()
        );
    }
}