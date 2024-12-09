package team9.ddang.chat.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;
import team9.ddang.chat.service.WebSocketMessageService;
import team9.ddang.chat.service.request.ChatReadServiceRequest;
import team9.ddang.chat.service.request.ChatServiceRequest;
import team9.ddang.chat.service.response.ChatReadResponse;
import team9.ddang.chat.service.response.ChatResponse;
import team9.ddang.global.api.WebSocketResponse;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatWebSocketConsumer {

    private final WebSocketMessageService webSocketMessageService;
    private final ObjectMapper objectMapper;

    @KafkaListener(
            topics = "topic-chat-broadcast",
            containerFactory = "webSocketListenerContainerFactory",
            groupId = "chat-websocket-consumer-group"
    )
    public void listen(ConsumerRecord<String, String> record, Acknowledgment acknowledgment) {
        String chatRoomId = record.key();
        String message = record.value();

        try {
            if (message.contains("\"readMessageIds\"")) {
                broadcastReadEvent(chatRoomId, message);
            } else {
                broadcastChatMessage(chatRoomId, message);
            }
            acknowledgment.acknowledge();
        } catch (Exception e) {
            log.error("Failed to process Kafka message for chatRoomId {} in ChatWebSocketConsumer: {}", chatRoomId, message, e);
            throw e;
        }
    }

    private void broadcastChatMessage(String chatRoomId, String message) {
        try {
            ChatResponse chatResponse = objectMapper.readValue(message, ChatResponse.class);

            String destination = "/sub/chat/" + chatResponse.chatRoomId();
            webSocketMessageService.broadcastMessage(destination, WebSocketResponse.ok(chatResponse));

            log.info("Message broadcasted to WebSocket for chatRoomId {}: {}", chatRoomId, destination);
        } catch (Exception e) {
            log.error("Failed to broadcast message to WebSocket for chatRoomId {}: {}", chatRoomId, message, e);
        }
    }

    private void broadcastReadEvent(String chatRoomId, String message) {
        try {
            ChatReadResponse chatReadResponse = objectMapper.readValue(message, ChatReadResponse.class);

            String destination = "/sub/chat/" + chatReadResponse.chatRoomId();
            webSocketMessageService.broadcastMessage(destination, WebSocketResponse.ok(chatReadResponse));

            log.info("Read event broadcasted to WebSocket for chatRoomId {}: {}", chatRoomId, destination);
        } catch (Exception e) {
            log.error("Failed to broadcast read event to WebSocket for chatRoomId {}: {}", chatRoomId, message, e);
        }
    }
}
