package team9.ddang.chat.controller.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.util.List;

@Schema(description = "채팅 메시지 읽음 요청 데이터")
public record ChatReadRequest(

        @Schema(description = "채팅방 ID", example = "1")
        @NotNull(message = "채팅방 아이디는 필수입니다.")
        Long chatRoomId,

        @Schema(description = "읽음 처리된 메시지 ID 목록, 확장성을 고려하여 만든 것으로, 최종 제출까지는 null", example = "null")
        List<Long> readMessageIds
) {}