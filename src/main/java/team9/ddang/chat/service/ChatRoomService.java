package team9.ddang.chat.service;

import team9.ddang.chat.service.response.ChatRoomResponse;

import java.util.List;

public interface ChatRoomService {
    public ChatRoomResponse createChatRoom(Long opponentMemberId);
    public List<ChatRoomResponse> getChatRoomsForAuthenticatedMember();
}
