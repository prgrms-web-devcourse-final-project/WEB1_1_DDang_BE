package team9.ddang.chat.service;

import team9.ddang.chat.service.response.ChatRoomResponse;

public interface ChatRoomService {
    public ChatRoomResponse createChatRoom(Long opponentMemberId);
}
