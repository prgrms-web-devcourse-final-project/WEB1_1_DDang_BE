package team9.ddang.chat.consumer;

//import org.springframework.security.core.context.SecurityContextHolder;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import team9.ddang.chat.controller.request.ChatRequest;
import team9.ddang.chat.entity.Chat;
import team9.ddang.chat.entity.ChatType;
import team9.ddang.chat.service.ChatService;
import team9.ddang.chat.service.WebSocketMessageService;
import team9.ddang.chat.service.response.ChatResponse;
import team9.ddang.member.entity.Member;



@Slf4j
@Service
@RequiredArgsConstructor
public class ChatConsumer {

    private final ChatService chatService;
    private final ObjectMapper objectMapper;
    private final WebSocketMessageService webSocketMessageService;

    public void consumeMessage(String topic, String message) {
        try {
            ChatRequest chatRequest = objectMapper.readValue(message, ChatRequest.class);

            Chat chat = chatService.saveChat(chatRequest.chatRoomId(), chatRequest.memberId(), chatRequest.message());

            ChatResponse chatResponse = new ChatResponse(chat);

            String destination = "/sub/chat/" + chatRequest.chatRoomId();
            webSocketMessageService.broadcastMessage(destination, chatResponse);

            log.info("Message broadcasted to WebSocket: {}", destination);
        } catch (Exception e) {
            log.error("Failed to process message: {}", message, e);
        }
    }
}