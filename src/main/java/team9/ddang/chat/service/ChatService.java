package team9.ddang.chat.service;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import team9.ddang.chat.service.request.ChatServiceRequest;
import team9.ddang.chat.service.response.ChatResponse;
import team9.ddang.member.entity.Member;

public interface ChatService {
    // TODO 나중에는 SpringSequrity에서 맴버 객체 받아서 사용할 예정
    ChatResponse saveChat(Long chatRoomId, String email, String message);

    Slice<ChatResponse> findChatsByRoom(Long chatRoomId, Pageable pageable, Member member);

    void updateMessageReadStatus(Long chatRoomId);

    void checkChat(ChatServiceRequest request);
}
