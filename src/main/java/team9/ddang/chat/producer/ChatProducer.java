package team9.ddang.chat.producer;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import team9.ddang.chat.exception.ChatExceptionMessage;
import team9.ddang.chat.service.request.ChatReadServiceRequest;
import team9.ddang.chat.service.request.ChatServiceRequest;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatProducer {

    private static final String TOPIC_CHAT = "topic-chat";
    private static final String TOPIC_CHAT_BROADCAST = "topic-chat-broadcast";

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public void sendMessage(Long chatRoomId, ChatServiceRequest chatServiceRequest) {
        try {
            String message = objectMapper.writeValueAsString(chatServiceRequest);

            kafkaTemplate.executeInTransaction(operations -> {
                operations.send(TOPIC_CHAT, chatRoomId.toString(), message);
                log.info("Message sent to Kafka for chatRoomId {}: {}", chatRoomId, message);
                return null;
            });
        } catch (Exception e) {
            log.error("Failed to serialize ChatServiceRequest: {}", chatServiceRequest, e);
            throw new IllegalArgumentException(ChatExceptionMessage.CHAT_JSON_PROCESSING_ERROR.getText(), e);
        }
    }

    public void sendMessageToBroadcast(Long chatRoomId, String message) {
        try {
            CompletableFuture<SendResult<String, String>> future = kafkaTemplate.send(TOPIC_CHAT_BROADCAST, chatRoomId.toString(), message);

            future.whenComplete((result, ex) -> {
                if (ex != null) {
                    log.error("Failed to send broadcast message for chatRoomId {}: {}", chatRoomId, message, ex);
                    throw new IllegalArgumentException("Failed to send broadcast message", ex);
                } else {
                    RecordMetadata metadata = result.getRecordMetadata();
                    log.info("Broadcast message sent to Kafka for chatRoomId {}: {}, partition: {}, offset: {}",
                            chatRoomId, message, metadata.partition(), metadata.offset());
                }
            });
        } catch (Exception e) {
            log.error("Unexpected error occurred while sending broadcast message for chatRoomId {}: {}", chatRoomId, message, e);
            throw new IllegalStateException("Unexpected error during broadcast message sending", e);
        }
    }

    public void sendReadEvent(Long chatRoomId, ChatReadServiceRequest chatReadServiceRequest) {
        try {
            String message = objectMapper.writeValueAsString(chatReadServiceRequest);

            kafkaTemplate.executeInTransaction(operations -> {
                operations.send(TOPIC_CHAT, chatRoomId.toString(), message);
                log.info("Read event sent to Kafka for chatRoomId {}: {}", chatRoomId, message);
                return null;
            });
        } catch (Exception e) {
            log.error("Failed to serialize ChatReadServiceRequest: {}", chatReadServiceRequest, e);
            throw new IllegalArgumentException("Failed to send MessageReadEvent", e);
        }
    }
}
