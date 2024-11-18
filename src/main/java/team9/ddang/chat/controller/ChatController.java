package team9.ddang.chat.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import team9.ddang.chat.producer.ChatProducer;

@RestController
@RequestMapping("/api/chats")
@RequiredArgsConstructor
public class ChatController {
    private final ChatProducer chatProducer;

    @PostMapping("/send")
    public void sendMessage(@RequestParam String topic, @RequestParam String message) {
        chatProducer.sendMessage(topic, message);
    }
}
