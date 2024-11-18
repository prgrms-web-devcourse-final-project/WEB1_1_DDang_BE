package team9.ddang.chat.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import team9.ddang.chat.controller.request.ChatRequest;
import team9.ddang.chat.producer.ChatProducer;

@RestController
@RequestMapping("/api/v1/chat")
@RequiredArgsConstructor
public class ChatController {
    private final ChatProducer chatProducer;
    private final ObjectMapper objectMapper;

    @PostMapping("/send")
    public ResponseEntity<Void> sendMessage(@RequestBody ChatRequest chatRequest) {
        try {
            String message = objectMapper.writeValueAsString(chatRequest);
            chatProducer.sendMessage("topic-chat-1", message);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}