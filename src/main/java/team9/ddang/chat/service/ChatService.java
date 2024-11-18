package team9.ddang.chat.service;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import team9.ddang.chat.entity.Chat;
import team9.ddang.chat.service.response.ChatResponse;

public interface ChatService {
    Chat saveChat(Long chatRoomId, Long memberId, String message);
    Slice<ChatResponse> findChatsByRoom(Long chatRoomId, Pageable pageable);
}
