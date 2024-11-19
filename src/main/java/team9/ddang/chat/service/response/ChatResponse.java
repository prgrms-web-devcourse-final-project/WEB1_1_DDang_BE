package team9.ddang.chat.service.response;

import team9.ddang.chat.entity.Chat;
import team9.ddang.chat.entity.ChatType;

import java.time.LocalDateTime;

public record ChatResponse(
        Long chatId,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        Long chatRoomId,
        ChatMemberInfo memberInfo,
        ChatType chatType,
        String text) {

    public ChatResponse(Chat chat) {
        this(
                chat.getChatId(),
                chat.getCreatedAt(),
                chat.getUpdatedAt(),
                chat.getChatRoom().getChatroomId(),
                new ChatMemberInfo(chat.getMember()),
                chat.getChatType(),
                chat.getText()
        );
    }

}