package team9.ddang.chat.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import team9.ddang.chat.producer.ChatProducer;
import team9.ddang.chat.service.ChatService;
import team9.ddang.chat.service.request.ChatReadServiceRequest;
import org.springframework.kafka.support.Acknowledgment;
import team9.ddang.chat.service.request.ChatServiceRequest;
import team9.ddang.chat.service.response.ChatReadResponse;
import team9.ddang.chat.service.response.ChatResponse;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatDatabaseConsumer {

    private final ChatService chatService;
    private final ObjectMapper objectMapper;
    private final ChatProducer chatProducer;

    @KafkaListener(
            topics = "topic-chat",
            containerFactory = "databaseListenerContainerFactory",
            groupId = "chat-database-consumer-group"
    )
    public void listen(ConsumerRecord<String, String> record, Acknowledgment acknowledgment) {
        String chatRoomId = record.key();
        String message = record.value();

        try {
            if (message.contains("\"readMessageIds\"")) {
                processReadEvent(chatRoomId, message);
            } else {
                processChatMessage(chatRoomId, message);
            }
            acknowledgment.acknowledge();
        } catch (Exception e) {
            log.error("Failed to process Kafka message for chatRoomId {} in ChatDatabaseConsumer: {}", chatRoomId, message, e);
            throw e;
        }
    }

    private void processChatMessage(String chatRoomId, String message) {
        try {
            ChatServiceRequest chatServiceRequest = objectMapper.readValue(message, ChatServiceRequest.class);

            ChatResponse chatResponse = chatService.saveChat(
                    chatServiceRequest.chatRoomId(),
                    chatServiceRequest.email(),
                    chatServiceRequest.message()
            );

            String chatResponseMessage = objectMapper.writeValueAsString(chatResponse);

            CompletableFuture.runAsync(() -> {
                chatProducer.sendMessageToBroadcast(
                        Long.parseLong(chatRoomId),
                        chatResponseMessage
                );
            });

            log.info("Message saved to database for chatRoomId {}: {}", chatRoomId, message);
        } catch (Exception e) {
            log.error("Failed to save message to database for chatRoomId {}: {}", chatRoomId, message, e);
        }
    }

    private void processReadEvent(String chatRoomId, String message) {
        try {
            ChatReadServiceRequest chatReadServiceRequest = objectMapper.readValue(message, ChatReadServiceRequest.class);

            ChatReadResponse chatReadResponse = chatService.updateMessageReadStatus(
                    chatReadServiceRequest.chatRoomId(),
                    chatReadServiceRequest.email()
            );

            String chatReadResponseMessage = objectMapper.writeValueAsString(chatReadResponse);

            CompletableFuture.runAsync(() -> {
                chatProducer.sendMessageToBroadcast(
                        Long.parseLong(chatRoomId),
                        chatReadResponseMessage
                );
            });

            log.info("Read event processed in database for chatRoomId {}: {}", chatRoomId, message);
        } catch (Exception e) {
            log.error("Failed to process read event in database for chatRoomId {}: {}", chatRoomId, message, e);
        }
    }
}