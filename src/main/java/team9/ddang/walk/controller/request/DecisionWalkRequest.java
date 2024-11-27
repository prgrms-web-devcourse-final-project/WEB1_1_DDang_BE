package team9.ddang.walk.controller.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import team9.ddang.walk.service.request.DecisionWalkServiceRequest;

public record DecisionWalkRequest(
        @NotNull(message = "상대방 이메일은 필수 입력 사항입니다.")
        @Schema(description = "상대방 이메일", example = "example@exaple.com")
        String otherEmail,
        @Schema(description = "수락 거절 여부", example = "ACCEPT")
        String decision
) {
    public DecisionWalkServiceRequest toService(){
        return new DecisionWalkServiceRequest(otherEmail, decision);
    }
}
