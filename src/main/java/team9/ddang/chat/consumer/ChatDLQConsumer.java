package team9.ddang.chat.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;
import team9.ddang.chat.service.ChatService;
import team9.ddang.chat.service.WebSocketMessageService;
import team9.ddang.chat.service.request.ChatReadServiceRequest;
import team9.ddang.chat.service.request.ChatServiceRequest;
import team9.ddang.global.api.WebSocketResponse;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatDLQConsumer {

    private final ChatService chatService;
    private final WebSocketMessageService webSocketMessageService;
    private final ObjectMapper objectMapper;

    @KafkaListener(
            topics = "topic-chat.DLT",
            containerFactory = "dlqListenerContainerFactory",
            groupId = "chat-dlq-consumer-group"
    )
    public void listenDLQ(ConsumerRecord<String, String> record, Acknowledgment acknowledgment) {
        String message = record.value();
        String messageKey = record.key();

        log.error("Received message in DLQ: {}", message);

        try {
            if (message.contains("\"readMessageIds\"")) {
                processReadEvent(messageKey, message);
            } else {
                processChatMessage(messageKey, message);
            }
            acknowledgment.acknowledge();
        } catch (Exception e) {
            log.error("Failed to reprocess DLQ message: {}", message, e);
        }
    }

    private void processChatMessage(String chatRoomId, String message) {
        try {
            ChatServiceRequest chatServiceRequest = objectMapper.readValue(message, ChatServiceRequest.class);

            chatService.saveChat(
                    chatServiceRequest.chatRoomId(),
                    chatServiceRequest.email(),
                    chatServiceRequest.message()
            );

            String destination = "/sub/chat/" + chatServiceRequest.chatRoomId();
            webSocketMessageService.broadcastMessage(destination, WebSocketResponse.ok(chatServiceRequest.message()));

            log.info("Reprocessed and broadcasted message for chatRoomId {}: {}", chatRoomId, message);
        } catch (Exception e) {
            log.error("Failed to reprocess chat message for chatRoomId {}: {}", chatRoomId, message, e);
        }
    }

    private void processReadEvent(String chatRoomId, String message) {
        try {
            ChatReadServiceRequest chatReadServiceRequest = objectMapper.readValue(message, ChatReadServiceRequest.class);

            chatService.updateMessageReadStatus(
                    chatReadServiceRequest.chatRoomId(),
                    chatReadServiceRequest.email()
            );

            String destination = "/sub/chat/" + chatReadServiceRequest.chatRoomId();
            webSocketMessageService.broadcastMessage(destination, WebSocketResponse.ok(chatReadServiceRequest));

            log.info("Reprocessed and broadcasted read event for chatRoomId {}: {}", chatRoomId, message);
        } catch (Exception e) {
            log.error("Failed to reprocess read event for chatRoomId {}: {}", chatRoomId, message, e);
        }
    }
}
