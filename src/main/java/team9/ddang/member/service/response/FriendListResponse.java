package team9.ddang.member.service.response;

import io.swagger.v3.oas.annotations.media.Schema;
import team9.ddang.global.entity.Gender;
import team9.ddang.member.entity.FamilyRole;
import team9.ddang.member.entity.Member;

public record FriendListResponse(

        @Schema(description = "회원 ID", example = "1")
        Long memberId,

        @Schema(description = "회원 성별", example = "MALE")
        Gender gender,

        @Schema(description = "가족 내 역할", example = "FATHER")
        FamilyRole familyRole,

        @Schema(description = "프로필 이미지", example = "http://asdasdasd.asdad")
        String profileImg,

        @Schema(description = "회원 이름", example = "춘식이")
        String name
) {
    public static FriendListResponse from(Member member){
        return new FriendListResponse(member.getMemberId(), member.getGender(), member.getFamilyRole(), member.getProfileImg(), member.getName());
    }
}
