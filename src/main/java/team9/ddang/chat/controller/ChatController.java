package team9.ddang.chat.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import team9.ddang.chat.controller.request.ChatReadRequest;
import team9.ddang.chat.controller.request.ChatRequest;
import team9.ddang.chat.producer.ChatProducer;
import team9.ddang.chat.service.ChatService;
import team9.ddang.chat.service.request.ChatReadServiceRequest;
import team9.ddang.chat.service.request.ChatServiceRequest;
import team9.ddang.chat.service.response.ChatResponse;
import team9.ddang.global.aop.AuthenticationContext;
import team9.ddang.global.aop.ExtractEmail;
import team9.ddang.global.api.ApiResponse;
import team9.ddang.member.oauth2.CustomOAuth2User;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/chat/message")
@Tag(name = "Chat API", description = "채팅 메시지 관련 API")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    private final ChatProducer chatProducer;

    @MessageMapping("/api/v1/chat/message")
    @ExtractEmail
    public void sendMessage(SimpMessageHeaderAccessor headerAccessor, @Valid ChatRequest chatRequest) {

        ChatServiceRequest chatServiceRequest = chatRequest.toServiceRequest(AuthenticationContext.getEmail());
        chatService.checkChat(chatServiceRequest);

        chatProducer.sendMessage("topic-chat-" + chatRequest.chatRoomId(), chatServiceRequest);
    }

    @MessageMapping("/api/v1/chat/ack")
    @ExtractEmail
    public void handleMessageAck(SimpMessageHeaderAccessor headerAccessor, @Valid ChatReadRequest chatReadRequest) {

        ChatReadServiceRequest chatReadServiceRequest = chatReadRequest.toServiceRequest(AuthenticationContext.getEmail());

        chatProducer.sendReadEvent("topic-chat-" + chatReadRequest.chatRoomId(), chatReadServiceRequest);
    }

    @GetMapping("/{chatRoomId}")
    @Operation(
            summary = "채팅방 메시지 조회",
            description = "특정 채팅방의 메시지를 페이징 형태로 조회합니다. 또한, 채팅방 입장으로 간주하여, /sub/chat/{chatRoomId} 구독 경로로 메세지 읽음 여부를 broadcast 합니다.",
            parameters = {
                    @Parameter(name = "chatRoomId", description = "조회할 채팅방 ID", required = true, example = "1"),
                    @Parameter(name = "lastMessageCreatedAt", description = "마지막으로 로드한 메시지의 생성 시간", example = "2024-12-04T12:34:56"),
            }
    )
    public ApiResponse<Slice<ChatResponse>> getChatMessages(
            @PathVariable Long chatRoomId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime lastMessageCreatedAt,
            @AuthenticationPrincipal CustomOAuth2User currentUser
    ) {
        PageRequest pageRequest = PageRequest.of(0, 10, Sort.by("createdAt").descending());


        Slice<ChatResponse> chats = (lastMessageCreatedAt == null) ?
                chatService.findChatsByRoom(chatRoomId, pageRequest, currentUser.getMember()) :
                chatService.findChatsBefore(chatRoomId, lastMessageCreatedAt, pageRequest, currentUser.getMember());

        return ApiResponse.ok(chats);
    }
}
