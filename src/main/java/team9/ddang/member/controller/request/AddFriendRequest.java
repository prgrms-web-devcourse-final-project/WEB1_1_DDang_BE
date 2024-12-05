package team9.ddang.member.controller.request;

import jakarta.validation.constraints.NotNull;
import team9.ddang.member.service.request.AddFriendServiceRequest;

public record AddFriendRequest(
        @NotNull(message = "memberId 는 필수 값 입니다.")
        Long memberId
) {
    public AddFriendServiceRequest toService(){
        return new AddFriendServiceRequest(memberId);
    }
}
