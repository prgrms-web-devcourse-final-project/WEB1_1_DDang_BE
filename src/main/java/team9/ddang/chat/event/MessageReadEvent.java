package team9.ddang.chat.event;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "메시지 읽음 이벤트 데이터")
public record MessageReadEvent(

        @Schema(description = "채팅방 ID", example = "1")
        Long chatRoomId,

        @Schema(description = "읽음 처리한 사용자 이메일", example = "test@test.com")
        String email,

        @Schema(description = "읽음 처리된 메시지 ID 목록, 확장성을 고려하여 만든 것으로, 최종 제출까지는 null", example = "null")
        List<Long> readMessageIds
) {
}