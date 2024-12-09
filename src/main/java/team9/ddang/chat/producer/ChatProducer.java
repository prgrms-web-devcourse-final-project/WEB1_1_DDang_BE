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

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public void sendMessage(Long chatRoomId, ChatServiceRequest chatServiceRequest) {
        try {
            String message = objectMapper.writeValueAsString(chatServiceRequest);

            CompletableFuture<SendResult<String, String>> future = kafkaTemplate.send(TOPIC_CHAT, chatRoomId.toString(), message);

            future.whenComplete((result, ex) -> {
                if (ex != null) {
                    log.error("Failed to send message to Kafka for chatRoomId {}: {}", chatRoomId, ex.getMessage(), ex);
                } else {
                    RecordMetadata metadata = result.getRecordMetadata();
                    log.info("Message sent to Kafka for chatRoomId {}: topic={}, partition={}, offset={}",
                            chatRoomId, metadata.topic(), metadata.partition(), metadata.offset());
                }
            });
        } catch (Exception e) {
            log.error("Failed to serialize ChatServiceRequest: {}", chatServiceRequest, e);
            throw new IllegalArgumentException(ChatExceptionMessage.CHAT_JSON_PROCESSING_ERROR.getText(), e);
        }
    }

    public void sendReadEvent(Long chatRoomId, ChatReadServiceRequest chatReadServiceRequest) {
        try {
            String message = objectMapper.writeValueAsString(chatReadServiceRequest);

            CompletableFuture<SendResult<String, String>> future = kafkaTemplate.send(TOPIC_CHAT, chatRoomId.toString(), message);

            future.whenComplete((result, ex) -> {
                if (ex != null) {
                    log.error("Failed to send read event to Kafka for chatRoomId {}: {}", chatRoomId, ex.getMessage(), ex);
                } else {
                    RecordMetadata metadata = result.getRecordMetadata();
                    log.info("Read event sent to Kafka for chatRoomId {}: topic={}, partition={}, offset={}",
                            chatRoomId, metadata.topic(), metadata.partition(), metadata.offset());
                }
            });
        } catch (Exception e) {
            log.error("Failed to serialize ChatReadServiceRequest: {}", chatReadServiceRequest, e);
            throw new IllegalArgumentException("Failed to send MessageReadEvent", e);
        }
    }
}
