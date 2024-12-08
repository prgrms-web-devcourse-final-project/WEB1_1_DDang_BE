package team9.ddang.member.service.response;

import io.swagger.v3.oas.annotations.media.Schema;
import team9.ddang.global.entity.Gender;
import team9.ddang.member.entity.FamilyRole;
import team9.ddang.member.entity.Member;
import team9.ddang.member.entity.Provider;

@Schema(description = "회원 응답 데이터")
public record MemberResponse(
        @Schema(description = "회원 ID", example = "1")
        Long memberId,

        @Schema(description = "회원 이름", example = "홍길동")
        String name,

        @Schema(description = "회원 이메일", example = "test@naver.com")
        String email,

        @Schema(description = "OAuth2 제공자", example = "NAVER")
        Provider provider,

        @Schema(description = "회원 성별", example = "MALE")
        Gender gender,

        @Schema(description = "회원 주소", example = "서울시 강남구")
        String address,

        @Schema(description = "가족 내 역할", example = "FATHER")
        FamilyRole familyRole,

        @Schema(description = "회원 프로필 이미지 URL", example = "https://example.com/profile.jpg")
        String profileImg
) {
    public static MemberResponse from(Member member) {
        return new MemberResponse(
                member.getMemberId(),
                member.getName(),
                member.getEmail(),
                member.getProvider(),
                member.getGender(),
                member.getAddress(),
                member.getFamilyRole(),
                member.getProfileImg());
    }
}
