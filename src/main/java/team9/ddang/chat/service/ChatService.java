package team9.ddang.chat.service;

import team9.ddang.chat.entity.Chat;

public interface ChatService {
    Chat saveChat(Long chatRoomId, Long memberId, String message);
}
