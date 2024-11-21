package team9.ddang.chat.service;

import team9.ddang.chat.service.request.ChatRoomCreateServiceRequest;
import team9.ddang.chat.service.response.ChatRoomResponse;

import java.util.List;

public interface ChatRoomService {
    ChatRoomResponse createChatRoom(ChatRoomCreateServiceRequest request);

    List<ChatRoomResponse> getChatRoomsForAuthenticatedMember();
}
