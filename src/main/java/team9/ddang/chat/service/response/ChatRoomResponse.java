package team9.ddang.chat.service.response;

import team9.ddang.chat.entity.ChatRoom;

public record ChatRoomResponse(
        Long chatroomId,
        String name) {

    public ChatRoomResponse(ChatRoom chatroom) {
        this(
                chatroom.getChatroomId(),
                chatroom.getName()
        );
    }
}
