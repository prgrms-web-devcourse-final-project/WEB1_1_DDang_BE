package team9.ddang.chat.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatRoomServiceImpl implements ChatRoomService{

    private final KafkaDynamicListenerService kafkaDynamicListenerService;

    public void createChatRoom(Long chatRoomId) {
        // 채팅방 생성 로직

        // Kafka Listener 등록
        kafkaDynamicListenerService.addListenerForChatRoom(chatRoomId);
    }
}
