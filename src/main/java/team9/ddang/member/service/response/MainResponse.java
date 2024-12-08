package team9.ddang.member.service.response;

import io.swagger.v3.oas.annotations.media.Schema;
import team9.ddang.dog.entity.Dog;
import team9.ddang.global.entity.Gender;
import team9.ddang.member.entity.FamilyRole;
import team9.ddang.member.entity.Member;
import team9.ddang.member.entity.Provider;
import team9.ddang.walk.service.response.TimeDuration;
import team9.ddang.walk.util.WalkCalculator;

@Schema(description = "Main 화면 응답")
public record MainResponse(
        @Schema(description = "회원 ID", example = "1")
        Long memberId,

        @Schema(description = "가족 역할", example = "ELDER_BROTHER")
        FamilyRole familyRole,

        @Schema(description = "주소", example = "인천광역시")
        String address,

        @Schema(description = "이메일", example = "example@example.com")
        String email,

        @Schema(description = "성별", example = "MALE")
        Gender memberGender,

        @Schema(description = "멤버 이름", example = "춘식이")
        String memberName,

        @Schema(description = "멤버 프로필 이미지", example = "https://example.com/profile.jpg")
        String memberProfileImgUrl,

        @Schema(description = "멤버 SNS", example = "KAKAO")
        Provider provider,

        @Schema(description = "강아지 이름", example = "Buddy")
        String dogName,


        @Schema(description = "산책 시간")
        TimeDuration timeDuration,

        @Schema(description = "총 이동 거리 (미터 단위)", example = "1000")
        int totalDistanceMeter,

        @Schema(description = "총 소비 칼로리", example = "50")
        int totalCalorie
) {
    public static MainResponse of(Member member, Dog dog, long totalSeconds, int totalDistanceMeter, Member loginMember){
        return new MainResponse(loginMember.getMemberId(),
                loginMember.getFamilyRole(),
                loginMember.getAddress(),
                loginMember.getEmail(),
                loginMember.getGender(),
                loginMember.getName(),
                loginMember.getProfileImg(),
                loginMember.getProvider(),
                dog.getName(),
                team9.ddang.walk.service.response.TimeDuration.from(totalSeconds),
                totalDistanceMeter,
                WalkCalculator.calculateCalorie(dog.getWeight(), totalDistanceMeter));
    }

    public static MainResponse of(Dog dog, long totalSeconds, int totalDistanceMeter,  Member loginMember){
        return new MainResponse(          loginMember.getMemberId(),
                loginMember.getFamilyRole(),
                loginMember.getAddress(),
                loginMember.getEmail(),
                loginMember.getGender(),
                loginMember.getName(),
                loginMember.getProfileImg(),
                loginMember.getProvider(),
                dog.getName(),
                team9.ddang.walk.service.response.TimeDuration.from(totalSeconds),
                totalDistanceMeter,
                WalkCalculator.calculateCalorie(dog.getWeight(), totalDistanceMeter));
    }
}

