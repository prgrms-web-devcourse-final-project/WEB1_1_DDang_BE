package team9.ddang.chat.consumer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ChatConsumer {

    @KafkaListener(topics = "topic-chat-1", groupId = "ddang-chat-group")
    public void consumeMessage(String message) {
        log.info("Received message: {}", message);
    }
}
