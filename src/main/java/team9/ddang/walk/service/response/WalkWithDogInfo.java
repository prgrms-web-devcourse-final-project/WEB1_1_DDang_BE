package team9.ddang.walk.service.response;

import io.swagger.v3.oas.annotations.media.Schema;
import team9.ddang.dog.entity.Dog;
import team9.ddang.global.entity.Gender;
import team9.ddang.member.entity.Member;

import static team9.ddang.walk.util.DateCalculator.calculateAgeFromNow;


public record WalkWithDogInfo(

        @Schema(description = "상대 강아지 ID", example = "1")
        Long otherDogId,

        @Schema(description = "상대 강아지 프로필", example = "http://~~~.com")
        String otherDogProfileImg,

        @Schema(description = "상대 강아지 이름", example = "초코")
        String otherDogName,

        @Schema(description = "상대 강아지 종", example = "시고르브 잡종")
        String otherDogBreed,

        @Schema(description = "상대 강아지 나이", example = "3")
        long otherDogAge,

        @Schema(description = "상대 강아지 성별", example = "MALE")
        Gender otherDogGender,

        @Schema(description = "회원 ID", example = "1")
        Long memberId
) {
        public static WalkWithDogInfo of(Member member, Dog dog){
                return new WalkWithDogInfo(dog.getDogId(), dog.getProfileImg(), dog.getName(), dog.getBreed(),
                        calculateAgeFromNow(dog.getBirthDate()), dog.getGender(), member.getMemberId());
        }

}
