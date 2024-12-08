package team9.ddang.chat.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import team9.ddang.chat.consumer.ChatConsumer;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaChatService {

    private final ChatConsumer chatConsumer;

    /**
     * 단일 Kafka Listener: "topic-chat"을 구독하고 메시지를 키(chatRoomId)로 구분하여 처리
     */
    @KafkaListener(topics = "topic-chat", containerFactory = "kafkaListenerContainerFactory")
    public void listen(ConsumerRecord<String, String> record) {
        String chatRoomId = record.key();
        String message = record.value();

        log.info("Received message for chatRoomId {}: {}", chatRoomId, message);

        try {
            if (message.contains("\"readMessageIds\"")) {
                chatConsumer.consumeReadEvent(chatRoomId, message);
            } else {
                chatConsumer.consumeMessage(chatRoomId, message);
            }
        } catch (Exception e) {
            log.error("Failed to process message for chatRoomId {}: {}", chatRoomId, message, e);
        }
    }
}
