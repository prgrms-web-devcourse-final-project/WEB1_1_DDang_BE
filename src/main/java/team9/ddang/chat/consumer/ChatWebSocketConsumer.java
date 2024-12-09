package team9.ddang.chat.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import team9.ddang.chat.service.WebSocketMessageService;
import team9.ddang.chat.service.request.ChatReadServiceRequest;
import team9.ddang.chat.service.request.ChatServiceRequest;
import team9.ddang.global.api.WebSocketResponse;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatWebSocketConsumer {

    private final WebSocketMessageService webSocketMessageService;
    private final ObjectMapper objectMapper;

    @KafkaListener(
            topics = "topic-chat",
            containerFactory = "kafkaListenerContainerFactory",
            groupId = "chat-websocket-consumer-group"
    )
    public void listen(ConsumerRecord<String, String> record) {
        String chatRoomId = record.key();
        String message = record.value();

        try {
            if (message.contains("\"readMessageIds\"")) {
                broadcastReadEvent(chatRoomId, message);
            } else {
                broadcastChatMessage(chatRoomId, message);
            }
        } catch (Exception e) {
            log.error("Failed to process Kafka message for chatRoomId {} in ChatWebSocketConsumer: {}", chatRoomId, message, e);
        }
    }

    private void broadcastChatMessage(String chatRoomId, String message) {
        try {
            ChatServiceRequest chatServiceRequest = objectMapper.readValue(message, ChatServiceRequest.class);

            String destination = "/sub/chat/" + chatServiceRequest.chatRoomId();
            webSocketMessageService.broadcastMessage(destination, WebSocketResponse.ok(chatServiceRequest.message()));

            log.info("Message broadcasted to WebSocket for chatRoomId {}: {}", chatRoomId, destination);
        } catch (Exception e) {
            log.error("Failed to broadcast message to WebSocket for chatRoomId {}: {}", chatRoomId, message, e);
        }
    }

    private void broadcastReadEvent(String chatRoomId, String message) {
        try {
            ChatReadServiceRequest chatReadServiceRequest = objectMapper.readValue(message, ChatReadServiceRequest.class);

            String destination = "/sub/chat/" + chatReadServiceRequest.chatRoomId();
            webSocketMessageService.broadcastMessage(destination, WebSocketResponse.ok(chatReadServiceRequest));

            log.info("Read event broadcasted to WebSocket for chatRoomId {}: {}", chatRoomId, destination);
        } catch (Exception e) {
            log.error("Failed to broadcast read event to WebSocket for chatRoomId {}: {}", chatRoomId, message, e);
        }
    }
}
