package team9.ddang.chat.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.messaging.handler.annotation.MessageMapping;
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

    private final ChatProducer chatProducer;

    @MessageMapping("/api/v1/chat/send")
    public void sendMessage(ChatRequest chatRequest) {
        chatProducer.sendMessage("topic-chat-" + chatRequest.chatRoomId(), chatRequest);
    }

    @GetMapping("/rooms/{chatRoomId}")
    public ApiResponse<Slice<ChatResponse>> getChatMessages(
            @PathVariable Long chatRoomId,
            @RequestParam(defaultValue = "0") int page
    ) {
        if (page < 0) {
            throw new IllegalArgumentException("페이지 번호는 0 이상이어야 합니다.");
        }
        PageRequest pageRequest = PageRequest.of(page, 10, Sort.by("createdAt").ascending());
        Slice<ChatResponse> chats = chatService.findChatsByRoom(chatRoomId, pageRequest);
        return ApiResponse.ok(chats);
    }
}
