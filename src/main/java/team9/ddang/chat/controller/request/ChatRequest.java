package team9.ddang.chat.controller.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import team9.ddang.chat.service.request.ChatServiceRequest;

@Schema(description = "채팅 메시지 요청 데이터")
public record ChatRequest(

        @Schema(description = "채팅방 ID", example = "3")
        @NotNull(message = "채팅방 아이디는 필수입니다.")
        Long chatRoomId,

        @Schema(description = "전송할 채팅 메시지", example = "안녕하세요!")
        @NotBlank(message = "채팅 메세지는 필수입니다.")
        String message
) {
    public ChatServiceRequest toServiceRequest() {
        return new ChatServiceRequest(chatRoomId, message);
    }
}
