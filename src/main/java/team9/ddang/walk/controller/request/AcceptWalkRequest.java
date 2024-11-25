package team9.ddang.walk.controller.request;

import jakarta.validation.constraints.NotNull;
import team9.ddang.walk.service.request.AcceptWalkServiceRequest;

public record AcceptWalkRequest(
        @NotNull(message = "상대방 이메일은 필수 입력 사항입니다.")
        String otherEmail
) {
    public AcceptWalkServiceRequest toService(){
        return new AcceptWalkServiceRequest(otherEmail);
    }
}
