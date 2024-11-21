package team9.ddang.chat.service.response;

import io.swagger.v3.oas.annotations.media.Schema;
import team9.ddang.chat.entity.Chat;
import team9.ddang.chat.entity.ChatType;
import team9.ddang.chat.entity.IsRead;

import java.time.LocalDateTime;

@Schema(description = "채팅 메시지 응답 데이터")
public record ChatResponse(
        @Schema(description = "메시지 ID", example = "123")
        Long chatId,

        @Schema(description = "메시지 생성 시간", example = "2024-11-21T12:00:00")
        LocalDateTime createdAt,

        @Schema(description = "메시지 수정 시간", example = "2024-11-21T12:05:00")
        LocalDateTime updatedAt,

        @Schema(description = "채팅방 ID", example = "1")
        Long chatRoomId,

        @Schema(description = "작성자 정보")
        ChatMemberInfo memberInfo,

        @Schema(description = "채팅 타입", example = "TALK")
        ChatType chatType,

        @Schema(description = "읽음 여부", example = "TRUE")
        IsRead isRead,

        @Schema(description = "메시지 내용", example = "안녕하세요!")
        String text) {

    public ChatResponse(Chat chat) {
        this(
                chat.getChatId(),
                chat.getCreatedAt(),
                chat.getUpdatedAt(),
                chat.getChatRoom().getChatroomId(),
                new ChatMemberInfo(chat.getMember()),
                chat.getChatType(),
                chat.getIsRead(),
                chat.getText()
        );
    }
}