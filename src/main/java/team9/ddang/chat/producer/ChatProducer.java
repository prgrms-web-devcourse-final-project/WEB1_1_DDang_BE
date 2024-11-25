package team9.ddang.chat.producer;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import team9.ddang.chat.controller.request.ChatRequest;
import team9.ddang.chat.event.MessageReadEvent;
import team9.ddang.chat.exception.ChatExceptionMessage;

@Service
@RequiredArgsConstructor
public class ChatProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public void sendMessage(String topic, ChatRequest chatRequest) {
        try {
            String message = objectMapper.writeValueAsString(chatRequest);
            kafkaTemplate.send(topic, message);
        } catch (Exception e) {
            throw new IllegalArgumentException(ChatExceptionMessage.CHAT_JSON_PROCESSING_ERROR.getText(), e);
        }
    }

    public void sendReadEvent(String topic, MessageReadEvent readEvent) {
        try {
            String message = objectMapper.writeValueAsString(readEvent);
            kafkaTemplate.send(topic, message);
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to send MessageReadEvent", e);
        }
    }
}