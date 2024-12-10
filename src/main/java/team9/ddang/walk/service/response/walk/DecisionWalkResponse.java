package team9.ddang.walk.service.response.walk;

import io.swagger.v3.oas.annotations.media.Schema;
import team9.ddang.dog.entity.Dog;
import team9.ddang.member.entity.Member;

public record DecisionWalkResponse(
        @Schema(description = "상대방 동의 여부", example = "ACCEPT")
        String decision,

        @Schema(description = "상대방 강아지 이름", example = "빠삐용")
        String otherDogName,

        @Schema(description = "상대방 멤버 Id", example = "빠삐용")
        Long otherMemberId,

        @Schema(description = "상대방 프로필 이미지", example = "Avatar5.svg")
        String otherMemberProfileImg,

        @Schema(description = "메시지 타입", example = "DECISION")
        Type type
) {
    public static DecisionWalkResponse of(String decision, Member member, Dog dog){
        return new DecisionWalkResponse(decision, dog.getName(), member.getMemberId() ,member.getProfileImg(), Type.DECISION);
    }
}
