package team9.ddang.chat.service;

import team9.ddang.chat.service.request.ChatRoomCreateServiceRequest;
import team9.ddang.chat.service.response.ChatRoomResponse;
import team9.ddang.member.entity.Member;

import java.util.List;

public interface ChatRoomService {
    ChatRoomResponse createChatRoom(ChatRoomCreateServiceRequest request, Member member);

    List<ChatRoomResponse> getChatRoomsForAuthenticatedMember(Member member);
}
