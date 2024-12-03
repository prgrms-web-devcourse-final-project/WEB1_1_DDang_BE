package team9.ddang.global.controller.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record WebSocketChatInfoResponse(
        @Schema(description = "채팅방 ID", example = "13")
        Long chatRoomId,
        @Schema(description = "읽지 않은 채팅 개수", example = "21")
        Long unreadCount
) {
}