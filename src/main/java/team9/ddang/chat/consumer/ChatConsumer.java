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
import team9.ddang.member.entity.Member;



@Slf4j
@Service
@RequiredArgsConstructor
public class ChatConsumer {

    private final ChatService chatService;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "topic-chat-1", groupId = "ddang-chat-group")
    public void consumeMessage(String message) {
        try {
            ChatRequest chatMessage = objectMapper.readValue(message, ChatRequest.class);

            chatService.saveChat(chatMessage.chatRoomId(), chatMessage.memberId(), chatMessage.message());
        } catch (Exception e) {
            log.error("Failed to process message: {}", message, e);
        }
    }
}



//@Slf4j
//@Service
//@RequiredArgsConstructor
//public class ChatConsumer {
//
//    private final ChatService chatService;
//
//    @KafkaListener(topics = "topic-chat-1", groupId = "ddang-chat-group")
//    public void consumeMessage(String message) {
//        try {
//            String authenticatedMemberId = SecurityContextHolder.getContext().getAuthentication().getName();
//
//            Chat chat = Chat.builder()
//                    .chatRoom(null)
//                    .member(new Member(Long.parseLong(authenticatedMemberId)))
//                    .chatType(ChatType.TALK)
//                    .text(message)
//                    .build();
//
//            chatService.saveChat(chat);
//        } catch (Exception e) {
//            log.error("Failed to process message: {}", message, e);
//        }
//    }
//}