package team9.ddang.chat.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
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
import team9.ddang.chat.service.response.SliceResponse;
import team9.ddang.global.aop.AuthenticationContext;
import team9.ddang.global.aop.ExtractEmail;
import team9.ddang.global.api.ApiResponse;
import team9.ddang.member.oauth2.CustomOAuth2User;

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

        chatProducer.sendReadEvent("topic-chat-"+ chatReadRequest.chatRoomId(), chatReadServiceRequest);
    }

    @GetMapping("/{chatRoomId}")
    @Operation(
            summary = "채팅방 메시지 조회",
            description = "특정 채팅방의 메시지를 페이징 형태로 조회합니다.",
            parameters = {
                    @Parameter(name = "chatRoomId", description = "조회할 채팅방 ID", required = true, example = "1"),
                    @Parameter(name = "page", description = "페이지 번호 (기본값: 0)", example = "0")
            },
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "200",
                            description = "채팅방 메시지 조회 성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = SliceResponse.class)
                            )
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "400",
                            description = "잘못된 요청 데이터",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ApiResponse.class)
                            )
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "500",
                            description = "서버 오류",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ApiResponse.class)
                            )
                    )
            }
    )
    public ApiResponse<Slice<ChatResponse>> getChatMessages(
            @PathVariable Long chatRoomId,
            @RequestParam(defaultValue = "0") int page,
            @AuthenticationPrincipal CustomOAuth2User currentUser
    ) {
        if (page < 0) {
            throw new IllegalArgumentException("페이지 번호는 0 이상이어야 합니다.");
        }
        PageRequest pageRequest = PageRequest.of(page, 10, Sort.by("createdAt").ascending());
        Slice<ChatResponse> chats = chatService.findChatsByRoom(chatRoomId, pageRequest, currentUser.getMember());
        return ApiResponse.ok(chats);
    }
}
