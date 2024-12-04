package team9.ddang.chat.service;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import team9.ddang.chat.service.request.ChatServiceRequest;
import team9.ddang.chat.service.response.ChatReadResponse;
import team9.ddang.chat.service.response.ChatResponse;
import team9.ddang.member.entity.Member;

import java.time.LocalDateTime;

public interface ChatService {

    ChatResponse saveChat(Long chatRoomId, String email, String message);

    Slice<ChatResponse> findChatsByRoom(Long chatRoomId, Pageable pageable, Member member);

    Slice<ChatResponse> findChatsBefore(Long chatRoomId, LocalDateTime lastMessageCreatedAt, Pageable pageable, Member member);

    ChatReadResponse updateMessageReadStatus(Long chatRoomId, String email);

    void checkChat(ChatServiceRequest request);
}
