package team9.ddang.chat.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.stereotype.Service;
import team9.ddang.chat.consumer.ChatConsumer;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaDynamicListenerService {

    private final ConcurrentKafkaListenerContainerFactory<String, String> factory;
    private final Map<String, ConcurrentMessageListenerContainer<String, String>> containers = new HashMap<>();
    private final ChatConsumer chatConsumer;

    public void addListenerForChatRoom(Long chatRoomId) {
        String topicName = "topic-chat-" + chatRoomId;

        if (containers.containsKey(topicName)) {
            log.info("Listener for topic '{}' already exists.", topicName);
            return;
        }

        ConcurrentMessageListenerContainer<String, String> container = factory.createContainer(topicName);
        container.getContainerProperties().setMessageListener((MessageListener<String, String>) record -> {
            log.info("Received message for topic '{}': {}", topicName, record.value());

            if (record.value().contains("\"readMessageIds\"")) {
                chatConsumer.consumeReadEvent(topicName, record.value());
            } else {
                chatConsumer.consumeMessage(topicName, record.value());
            }
        });

        container.start();
        containers.put(topicName, container);

        log.info("Started Kafka listener for topic '{}'", topicName);
    }

    public void removeListenerForChatRoom(Long chatRoomId) {
        String topicName = "topic-chat-" + chatRoomId;

        ConcurrentMessageListenerContainer<String, String> container = containers.remove(topicName);
        if (container != null) {
            container.stop();
            log.info("Stopped Kafka listener for topic '{}'", topicName);
        }
    }
}