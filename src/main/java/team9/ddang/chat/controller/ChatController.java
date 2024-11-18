package team9.ddang.chat.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import team9.ddang.chat.controller.request.ChatRequest;
import team9.ddang.chat.producer.ChatProducer;
import team9.ddang.global.api.ApiResponse;

@RestController
@RequestMapping("/api/v1/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatProducer chatProducer;

    @PostMapping("/send")
    public ResponseEntity<ApiResponse<Void>> sendMessage(@RequestBody ChatRequest chatRequest) {
        chatProducer.sendMessage("topic-chat-1", chatRequest);
        return ResponseEntity.ok(ApiResponse.ok(null));
    }
}
