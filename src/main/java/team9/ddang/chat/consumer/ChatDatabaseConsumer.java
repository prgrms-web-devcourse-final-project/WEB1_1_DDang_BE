package team9.ddang.chat.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import team9.ddang.chat.service.ChatService;
import team9.ddang.chat.service.request.ChatReadServiceRequest;
import team9.ddang.chat.service.request.ChatServiceRequest;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatDatabaseConsumer {

    private final ChatService chatService;
    private final ObjectMapper objectMapper;

    @KafkaListener(
            topics = "topic-chat",
            containerFactory = "kafkaListenerContainerFactory",
            groupId = "chat-database-consumer-group"
    )
    public void listen(ConsumerRecord<String, String> record) {
        String chatRoomId = record.key();
        String message = record.value();

        try {
            if (message.contains("\"readMessageIds\"")) {
                processReadEvent(chatRoomId, message);
            } else {
                processChatMessage(chatRoomId, message);
            }
        } catch (Exception e) {
            log.error("Failed to process Kafka message for chatRoomId {} in ChatDatabaseConsumer: {}", chatRoomId, message, e);
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

            log.info("Message saved to database for chatRoomId {}: {}", chatRoomId, message);
        } catch (Exception e) {
            log.error("Failed to save message to database for chatRoomId {}: {}", chatRoomId, message, e);
        }
    }

    private void processReadEvent(String chatRoomId, String message) {
        try {
            ChatReadServiceRequest chatReadServiceRequest = objectMapper.readValue(message, ChatReadServiceRequest.class);

            chatService.updateMessageReadStatus(
                    chatReadServiceRequest.chatRoomId(),
                    chatReadServiceRequest.email()
            );

            log.info("Read event processed in database for chatRoomId {}: {}", chatRoomId, message);
        } catch (Exception e) {
            log.error("Failed to process read event in database for chatRoomId {}: {}", chatRoomId, message, e);
        }
    }
}