package team9.ddang.chat.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "특정 채팅방의 메세지 목록 조회 성공",
                    useReturnTypeSchema = true
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "요청 데이터가 유효하지 않은 경우",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = {
                                    @ExampleObject(
                                            name = "존재하지 않는 회원",
                                            value = "{ \"code\": 400, \"status\": \"BAD_REQUEST\", \"message\": \"해당 맴버를 찾을 수 없습니다.\", \"data\": null }"
                                    ),
                                    @ExampleObject(
                                            name = "존재하지 않는 채팅방",
                                            value = "{ \"code\": 400, \"status\": \"BAD_REQUEST\", \"message\": \"해당 채팅방을 찾을 수 없습니다.\", \"data\": null }"
                                    ),
                                    @ExampleObject(
                                            name = "본인이 속하지 않는 채팅방",
                                            value = "{ \"code\": 400, \"status\": \"BAD_REQUEST\", \"message\": \"해당 회원을 채팅방에서 찾을 수 없습니다.\", \"data\": null }"
                                    ),
                            }
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "인증 실패 또는 유효하지 않은 토큰으로 접근하려는 경우",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(
                                    name = "인증 실패 예시",
                                    value = "{ \"code\": 401, \"status\": \"UNAUTHORIZED\", \"message\": \"AccessToken is invalid\", \"data\": null }"
                            )
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "500",
                    description = "서버 내부에서 처리되지 않은 오류가 발생한 경우",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(
                                    name = "서버 오류 예시",
                                    value = "{ \"code\": 500, \"status\": \"INTERNAL_SERVER_ERROR\", \"message\": \"알 수 없는 오류가 발생했습니다.\", \"data\": null }"
                            )
                    )
            )
    })
    public ApiResponse<Slice<ChatResponse>> getChatMessages(
            @PathVariable Long chatRoomId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime lastMessageCreatedAt,
            @AuthenticationPrincipal CustomOAuth2User currentUser
    ) {
        PageRequest pageRequest = PageRequest.of(0, 20, Sort.by("createdAt").descending());


        Slice<ChatResponse> chats = (lastMessageCreatedAt == null) ?
                chatService.findChatsByRoom(chatRoomId, pageRequest, currentUser.getMember()) :
                chatService.findChatsBefore(chatRoomId, lastMessageCreatedAt, pageRequest, currentUser.getMember());

        return ApiResponse.ok(chats);
    }
}
