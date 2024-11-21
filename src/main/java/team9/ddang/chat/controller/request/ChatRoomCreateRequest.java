package team9.ddang.chat.controller.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import team9.ddang.chat.service.request.ChatRoomCreateServiceRequest;

@Schema(description = "채팅방 생성 요청 데이터")
public record ChatRoomCreateRequest(
        @Schema(description = "상대방 회원의 ID", example = "2")
        @NotNull(message = "상대방의 회원 ID는 필수입니다.")
        Long opponentMemberId
) {
    public ChatRoomCreateServiceRequest toServiceRequest() {
        return new ChatRoomCreateServiceRequest(opponentMemberId);
    }
}