package team9.ddang.global.controller.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "WebSocket 에러 응답 데이터")
public record WebSocketErrorResponse(
        @Schema(description = "에러 코드", example = "400") int code,
        @Schema(description = "상태", example = "BAD_REQUEST") String status,
        @Schema(description = "에러 메시지", example = "유효성 검사 실패: 채팅방 아이디는 필수입니다.") String message
) {
}
