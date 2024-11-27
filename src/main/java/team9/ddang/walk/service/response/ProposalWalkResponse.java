package team9.ddang.walk.service.response;

import io.swagger.v3.oas.annotations.media.Schema;
import team9.ddang.dog.entity.Dog;
import team9.ddang.global.entity.Gender;
import team9.ddang.member.entity.Member;

import static team9.ddang.walk.util.DateCalculator.calculateAgeFromNow;

@Schema(description = "산책 제안 응답 객체")
public record ProposalWalkResponse(
        @Schema(description = "강아지의 식별자", example = "1")
        Long dogId,

        @Schema(description = "강아지 이름", example = "몽이")
        String dogName,

        @Schema(description = "강아지 품종", example = "골든 리트리버")
        String dogBreed,

        @Schema(description = "강아지 프로필 이미지 URL", example = "https://example.com/dog/profile.jpg")
        String dogProfileImg,

        @Schema(description = "추가 코멘트", example = "활발한 강아지입니다!")
        String comment,

        @Schema(description = "강아지 성별", example = "MALE")
        Gender dogGender,

        @Schema(description = "강아지 나이", example = "5")
        long dogAge,

        @Schema(description = "회원 이메일", example = "example@example.com")
        String email
){
    public static ProposalWalkResponse of(Dog dog, Member member, String comment){
        if(comment == null){
            // TODO : dog 엔티티 변경 시 default comment 대체 예정
        }

        return new ProposalWalkResponse(dog.getDogId(), dog.getName(), dog.getBreed(), dog.getProfileImg(),
                comment, dog.getGender(), calculateAgeFromNow(dog.getBirthDate()), member.getEmail());
    }
}
