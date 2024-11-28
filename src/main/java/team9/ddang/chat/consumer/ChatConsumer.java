package team9.ddang.chat.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import team9.ddang.chat.controller.request.ChatRequest;
import team9.ddang.chat.event.MessageReadEvent;
import team9.ddang.chat.service.ChatService;
import team9.ddang.chat.service.WebSocketMessageService;
import team9.ddang.chat.service.response.ChatResponse;


@Slf4j
@Service
@RequiredArgsConstructor
public class ChatConsumer {

    private final ChatService chatService;
    private final ObjectMapper objectMapper;
    private final WebSocketMessageService webSocketMessageService;

    public void consumeMessage(String topic, String message) {
        try {
            ChatRequest chatRequest = objectMapper.readValue(message, ChatRequest.class);

            ChatResponse chatResponse = chatService.saveChat(chatRequest.chatRoomId(), 2L, chatRequest.message());

            String destination = "/sub/chat/" + chatRequest.chatRoomId();
            webSocketMessageService.broadcastMessage(destination, chatResponse);

            log.info("Message broadcasted to WebSocket: {}", destination);
        } catch (Exception e) {
            log.error("Failed to process message: {}", message, e);
        }
    }

    public void consumeReadEvent(String topic, String message) {
        try {
            MessageReadEvent readEvent = objectMapper.readValue(message, MessageReadEvent.class);

            String destination = "/sub/chat/" + readEvent.chatRoomId();
            webSocketMessageService.broadcastMessage(destination, readEvent);

            log.info("Read event broadcasted to WebSocket: {}", destination);
        } catch (Exception e) {
            log.error("Failed to process read event: {}", message, e);
        }
    }

}