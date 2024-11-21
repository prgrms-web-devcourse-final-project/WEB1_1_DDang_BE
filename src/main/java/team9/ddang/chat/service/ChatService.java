package team9.ddang.chat.service;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import team9.ddang.chat.service.response.ChatResponse;

public interface ChatService {
    // TODO 나중에는 SpringSequrity에서 맴버 객체 받아서 사용할 예정
    ChatResponse saveChat(Long chatRoomId, Long memberId, String message);

    Slice<ChatResponse> findChatsByRoom(Long chatRoomId, Pageable pageable);

    void updateMessageReadStatus(Long chatRoomId);

    void checkChat(Long chatRoomId);
}
