package team9.ddang.walk.service.response.walk;

import io.swagger.v3.oas.annotations.media.Schema;
import team9.ddang.member.entity.Member;

public record DecisionWalkResponse(
        @Schema(description = "상대방 동의 여부", example = "ACCEPT")
        String decision,

        @Schema(description = "상대방 이름", example = "춘식이")
        String otherMemberName,

        @Schema(description = "상대방 프로필 이미지", example = "Avatar5.svg")
        String otherMemberProfileImg,

        @Schema(description = "메시지 타입", example = "DECISION")
        Type type
) {
    public static DecisionWalkResponse of(String decision, Member member){
        return new DecisionWalkResponse(decision, member.getName(), member.getProfileImg(), Type.DECISION);
    }
}
