package team9.ddang.global.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import team9.ddang.chat.controller.request.ChatReadRequest;
import team9.ddang.chat.controller.request.ChatRequest;
import team9.ddang.chat.service.response.ChatReadResponse;
import team9.ddang.chat.service.response.ChatResponse;
import team9.ddang.global.api.WebSocketResponse;
import team9.ddang.global.controller.response.WebSocketErrorResponse;

@Tag(name = "WebSocket Chat API", description = "WebSocket을 통한 채팅 관련 명세")
@RestController
public class WebSocketController {

    @Operation(
            summary = "채팅 메시지 전송",
            description = "WebSocket을 통해 채팅 메시지를 전송합니다. 메시지를 전송하려면 '/pub/api/v1/chat/message' 경로로 JSON 데이터를 전송하세요. /sub/chat/{chatRoomId} 구독 경로로 메세지를 broadcast 합니다.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "채팅 메시지 요청 데이터",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ChatRequest.class)
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "400",
                            description = "잘못된 요청 (유효성 검사 실패 또는 기타 클라이언트 오류)",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = WebSocketErrorResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "서버 에러 (감지하지 못한 서버 에러)",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = WebSocketErrorResponse.class)
                            )
                    )
            }
    )
    @GetMapping("/docs/ws/api/v1/chat/message")
    public WebSocketResponse<ChatResponse> sendMessage() {
        ChatResponse chatResponse = null;
        return WebSocketResponse.ok(chatResponse);
    }

    @Operation(
            summary = "메시지 읽음 처리",
            description = "WebSocket을 통해 메시지 읽음 상태를 업데이트합니다. 읽음 처리를 위해 '/pub/api/v1/chat/ack/{chatRoomId}' 경로로 JSON 데이터를 전송하세요. /sub/chat/{chatRoomId} 구독 경로로 메세지 읽음 여부를 broadcast 합니다.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "메시지 읽음 요청 데이터",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ChatReadRequest.class)
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "400",
                            description = "잘못된 요청 (유효성 검사 실패 또는 기타 클라이언트 오류)",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = WebSocketErrorResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "서버 에러 (감지하지 못한 서버 에러)",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = WebSocketErrorResponse.class)
                            )
                    )
            }
    )
    @GetMapping("/docs/ws/api/v1/chat/ack")
    public WebSocketResponse<ChatReadResponse> handleMessageAck() {
        ChatReadResponse chatReadResponse = null;
        return WebSocketResponse.ok(chatReadResponse);
    }
}