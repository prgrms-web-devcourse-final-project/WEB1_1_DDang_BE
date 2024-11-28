package team9.ddang.chat.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import team9.ddang.chat.service.ChatService;
import team9.ddang.chat.service.WebSocketMessageService;
import team9.ddang.chat.service.request.ChatReadServiceRequest;
import team9.ddang.chat.service.request.ChatServiceRequest;
import team9.ddang.chat.service.response.ChatReadResponse;
import team9.ddang.chat.service.response.ChatResponse;
import team9.ddang.global.api.WebSocketResponse;


@Slf4j
@Service
@RequiredArgsConstructor
public class ChatConsumer {

    private final ChatService chatService;
    private final ObjectMapper objectMapper;
    private final WebSocketMessageService webSocketMessageService;

    public void consumeMessage(String topic, String message) {
        try {
            ChatServiceRequest chatServiceRequest = objectMapper.readValue(message, ChatServiceRequest.class);

            ChatResponse chatResponse = chatService.saveChat(chatServiceRequest.chatRoomId(), chatServiceRequest.email(), chatServiceRequest.message());

            String destination = "/sub/chat/" + chatServiceRequest.chatRoomId();
            webSocketMessageService.broadcastMessage(destination, WebSocketResponse.ok(chatResponse));

            log.info("Message broadcasted to WebSocket: {}", destination);
        } catch (Exception e) {
            log.error("Failed to process message: {}", message, e);
        }
    }

    public void consumeReadEvent(String topic, String message) {
        try {
            ChatReadServiceRequest chatReadServiceRequest = objectMapper.readValue(message, ChatReadServiceRequest.class);

            ChatReadResponse chatReadResponse = chatService.updateMessageReadStatus(chatReadServiceRequest.chatRoomId(), chatReadServiceRequest.email());

            String destination = "/sub/chat/" + chatReadServiceRequest.chatRoomId();
            webSocketMessageService.broadcastMessage(destination, WebSocketResponse.ok(chatReadResponse));

            log.info("Read event broadcasted to WebSocket: {}", destination);
        } catch (Exception e) {
            log.error("Failed to process read event: {}", message, e);
        }
    }

}