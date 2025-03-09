package team9.ddang.global.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import team9.ddang.chat.controller.request.ChatReadRequest;
import team9.ddang.chat.controller.request.ChatRequest;
import team9.ddang.chat.service.response.ChatReadResponse;
import team9.ddang.chat.service.response.ChatResponse;
import team9.ddang.chat.service.response.ChatRoomResponse;
import team9.ddang.global.api.WebSocketResponse;
import team9.ddang.global.controller.response.WebSocketChatInfoResponse;
import team9.ddang.global.controller.response.WebSocketErrorResponse;

import java.util.List;

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
                            responseCode = "200",
                            description = "채팅 메시지 전송",
                            useReturnTypeSchema = true
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "잘못된 요청 (유효성 검사 실패 또는 기타 클라이언트 오류)",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = WebSocketResponse.class),
                                    examples = {
                                            @ExampleObject(
                                                    name = "채팅방 아이디가 누락된 경우",
                                                    value = "{ \"code\": 4000, \"status\": \"UNAUTHORIZED\", \"message\": \"채팅방 아이디는 필수입니다.\", \"data\": null }"
                                            ),
                                            @ExampleObject(
                                                    name = "채팅방 메세지가 누락된 경우",
                                                    value = "{ \"code\": 4000, \"status\": \"UNAUTHORIZED\", \"message\": \"채팅 메세지는 필수입니다.\", \"data\": null }"
                                            ),
                                            @ExampleObject(
                                                    name = "유효하지 않은 채팅방의 경우",
                                                    value = "{ \"code\": 4000, \"status\": \"UNAUTHORIZED\", \"message\": \"해당 채팅방을 찾을 수 없습니다.\", \"data\": null }"
                                            ),
                                            @ExampleObject(
                                                    name = "메세지 변환 오류",
                                                    value = "{ \"code\": 4000, \"status\": \"UNAUTHORIZED\", \"message\": \"메시지 변환 중 문제가 발생했습니다.\", \"data\": null }"
                                            )
                                    }
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "잘못된 요청 (유효성 검사 실패 또는 기타 클라이언트 오류)",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = WebSocketResponse.class),
                                    examples = {
                                            @ExampleObject(
                                                    name = "채팅방 아이디가 누락된 경우",
                                                    value = "{ \"code\": 4000, \"status\": \"UNAUTHORIZED\", \"message\": \"채팅방 아이디는 필수입니다.\", \"data\": null }"
                                            ),
                                            @ExampleObject(
                                                    name = "채팅방 메세지가 누락된 경우",
                                                    value = "{ \"code\": 4000, \"status\": \"UNAUTHORIZED\", \"message\": \"채팅 메세지는 필수입니다.\", \"data\": null }"
                                            ),
                                            @ExampleObject(
                                                    name = "유효하지 않은 채팅방의 경우",
                                                    value = "{ \"code\": 4000, \"status\": \"UNAUTHORIZED\", \"message\": \"해당 채팅방을 찾을 수 없습니다.\", \"data\": null }"
                                            ),
                                            @ExampleObject(
                                                    name = "메세지 변환 오류",
                                                    value = "{ \"code\": 4000, \"status\": \"UNAUTHORIZED\", \"message\": \"메시지 변환 중 문제가 발생했습니다.\", \"data\": null }"
                                            )
                                    }
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "인증 실패 또는 유효하지 않은 토큰으로 접근하려는 경우",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = team9.ddang.global.api.ApiResponse.class),
                                    examples = {
                                            @ExampleObject(
                                                    name = "인증 실패 예시",
                                                    value = "{ \"code\": 4001, \"status\": \"UNAUTHORIZED\", \"message\": \"AccessToken is invalid\", \"data\": null }"
                                            ),
                                            @ExampleObject(
                                                    name = "토큰이 없는 경우",
                                                    value = "{ \"code\": 4002, \"status\": \"UNAUTHORIZED\", \"message\": \"토큰을 찾을 수 없습니다.\", \"data\": null }"
                                            ),
                                            @ExampleObject(
                                                    name = "이메일 추출 실패",
                                                    value = "{ \"code\": 4002, \"status\": \"UNAUTHORIZED\", \"message\": \"AccessToken에서 email을 추출할 수 없습니다.\", \"data\": null }"
                                            )
                                    }
                            )
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "서버 에러 (감지하지 못한 서버 에러)",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = WebSocketErrorResponse.class),
                                    examples = @ExampleObject(
                                            name = "서버 오류",
                                            value = "{ \"code\": 5000, \"status\": \"INTERNAL_SERVER_ERROR\", \"message\": \"서버 내부 오류\", \"data\": null }"
                                    )
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
                            responseCode = "200",
                            description = "메세지 읽음 처리",
                            useReturnTypeSchema = true
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "잘못된 요청 (유효성 검사 실패 또는 기타 클라이언트 오류)",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = WebSocketResponse.class),
                                    examples = {
                                            @ExampleObject(
                                                    name = "채팅방 아이디가 누락된 경우",
                                                    value = "{ \"code\": 4000, \"status\": \"UNAUTHORIZED\", \"message\": \"채팅방 아이디는 필수입니다.\", \"data\": null }"
                                            ),
                                            @ExampleObject(
                                                    name = "유효하지 않은 채팅방의 경우",
                                                    value = "{ \"code\": 4000, \"status\": \"UNAUTHORIZED\", \"message\": \"해당 채팅방을 찾을 수 없습니다.\", \"data\": null }"
                                            )
                                    }
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "인증 실패 또는 유효하지 않은 토큰으로 접근하려는 경우",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = team9.ddang.global.api.ApiResponse.class),
                                    examples = {
                                            @ExampleObject(
                                                    name = "인증 실패 예시",
                                                    value = "{ \"code\": 4001, \"status\": \"UNAUTHORIZED\", \"message\": \"AccessToken is invalid\", \"data\": null }"
                                            ),
                                            @ExampleObject(
                                                    name = "토큰이 없는 경우",
                                                    value = "{ \"code\": 4002, \"status\": \"UNAUTHORIZED\", \"message\": \"토큰을 찾을 수 없습니다.\", \"data\": null }"
                                            ),
                                            @ExampleObject(
                                                    name = "이메일 추출 실패",
                                                    value = "{ \"code\": 4002, \"status\": \"UNAUTHORIZED\", \"message\": \"AccessToken에서 email을 추출할 수 없습니다.\", \"data\": null }"
                                            )
                                    }
                            )
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "서버 에러 (감지하지 못한 서버 에러)",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = WebSocketErrorResponse.class),
                                    examples = @ExampleObject(
                                            name = "서버 오류",
                                            value = "{ \"code\": 5000, \"status\": \"INTERNAL_SERVER_ERROR\", \"message\": \"서버 내부 오류\", \"data\": null }"
                                    )
                            )
                    )
            }
    )
    @GetMapping("/docs/ws/api/v1/chat/ack")
    public WebSocketResponse<ChatReadResponse> handleMessageAck() {
        ChatReadResponse chatReadResponse = null;
        return WebSocketResponse.ok(chatReadResponse);
    }

    @Operation(
            summary = "채팅방 정보 url",
            description = " 해당 url을 구독하면, 내가 속한 채팅방 목록 및 읽지 않은 메세지 개수를 받을 수 있습니다. 또한 타 유저에 의해 새로운 채팅방이 생성되면 해당 채팅방 정보를 받습니다.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "참여중인 채팅방 정보 리스트",
                            useReturnTypeSchema = true
                    ),
                    @ApiResponse(
                            responseCode = "201",
                            description = "새로운 채팅방 생성 시 code = 1001, message = created 로 전송합니다. data는 다음과 같습니다.",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ChatRoomResponse.class)
                            )
                    )
            }
    )
    @PostMapping("/sub/message/{email}")
    public WebSocketResponse<List<WebSocketChatInfoResponse>> subMemberEmail(){
        List<WebSocketChatInfoResponse> webSocketChatInfoResponse = null;
        return WebSocketResponse.ok(webSocketChatInfoResponse);
    }

    @Operation(
            summary = "채팅 url",
            description = " 해당 url을 구독하면, 해당 채팅방의 채팅을 수신할 수 있습니다. 또한 다른 member 의 메세지 읽음 여부도 수신할 수 있습니다."
    )
    @PostMapping("/sub/chat/{chatRoomId}")
    public WebSocketResponse<ChatResponse> recieveMessage() {
        ChatResponse chatResponse = null;
        return WebSocketResponse.ok(chatResponse);
    }
}