package team9.ddang.chat.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import team9.ddang.chat.controller.request.ChatRequest;
import team9.ddang.chat.producer.ChatProducer;
import team9.ddang.chat.service.ChatService;
import team9.ddang.chat.service.response.ChatResponse;
import team9.ddang.global.api.ApiResponse;

@RestController
@RequestMapping("/api/v1/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

//    private final ChatProducer chatProducer;

//    @PostMapping("/send")
//    public ResponseEntity<ApiResponse<Void>> sendMessage(@RequestBody ChatRequest chatRequest) {
//        chatProducer.sendMessage("topic-chat-1", chatRequest);
//        return ResponseEntity.ok(ApiResponse.ok(null));
//    }

    @GetMapping("/rooms/{chatRoomId}")
    public ResponseEntity<ApiResponse<Slice<ChatResponse>>> getChatMessages(
            @PathVariable Long chatRoomId,
            @RequestParam(defaultValue = "0") int page
    ) {
        PageRequest pageRequest = PageRequest.of(page, 10, Sort.by("createdAt").ascending());
        Slice<ChatResponse> chats = chatService.findChatsByRoom(chatRoomId, pageRequest);
        return ResponseEntity.ok(ApiResponse.ok(chats));
    }
}
