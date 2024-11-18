package team9.ddang.chat.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.web.bind.annotation.RestController;
import team9.ddang.chat.controller.request.ChatRequest;
import team9.ddang.chat.producer.ChatProducer;

@RestController
@RequiredArgsConstructor
public class ChatWebSocketController {

    private final ChatProducer chatProducer;

    @MessageMapping("/chat/send")
    public void sendMessage(ChatRequest chatRequest) {
        chatProducer.sendMessage("topic-chat-" + chatRequest.chatRoomId(), chatRequest);
    }
}