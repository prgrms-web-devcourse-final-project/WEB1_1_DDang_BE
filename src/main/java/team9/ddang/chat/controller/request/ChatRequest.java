package team9.ddang.chat.controller.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import team9.ddang.chat.service.request.ChatServiceRequest;

public record ChatRequest(
        // TODO 나중에는 SpringSequrity에서 맴버 객체 받아서 사용할 예정
        @NotNull(message = "회원 아이디는 필수입니다.")
        Long memberId,
        @NotNull(message = "채팅방 아이디는 필수입니다.")
        Long chatRoomId,
        @NotBlank(message = "채팅 메세지는 필수입니다.")
        String message
) {
    public ChatServiceRequest toServiceRequest() {
        return new ChatServiceRequest(memberId, chatRoomId, message);
    }
}
