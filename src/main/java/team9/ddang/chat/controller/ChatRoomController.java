package team9.ddang.chat.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import team9.ddang.chat.controller.request.ChatRoomCreateRequest;
import team9.ddang.chat.service.ChatRoomService;
import team9.ddang.chat.service.response.ChatRoomResponse;
import team9.ddang.global.api.ApiResponse;
import team9.ddang.member.oauth2.CustomOAuth2User;

import java.util.List;

@RestController
@RequestMapping("/api/v1/chat/rooms")
@RequiredArgsConstructor
@Tag(name = "Chat Room API", description = "채팅방 생성 및 조회 API")
public class ChatRoomController {

    private final ChatRoomService chatRoomService;

    @PostMapping
    @Operation(
            summary = "채팅방 생성",
            description = """
                    새로운 채팅방을 생성하고, 채팅방 정보를 반환합니다.
                    이미 동일한 유저와의 채팅방이 있다면, 해당 채팅방을 반환합니다.
                    요청 본문에 상대방의 유저 ID(opponentMemberId)를 포함해야 합니다.
                    새로운 채팅방이 생성된다면, "/sub/chatroom/{opponentMemberEmail} 구독 경로로 채팅방이 생성되었음을 알립니다.
                    """,
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "채팅방 생성 요청 데이터",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ChatRoomCreateRequest.class)
                    )
            )
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "채팅방 생성 성공 (이미 존재하는 경우에도 동일)",
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
                                            name = "존재하지 않는 유저",
                                            value = "{ \"code\": 400, \"status\": \"BAD_REQUEST\", \"message\": \"해당 유저를 찾을 수 없습니다.\", \"data\": null }"
                                    ),
                                    @ExampleObject(
                                            name = "유효하지 않은 요청 데이터",
                                            value = "{ \"code\": 400, \"status\": \"BAD_REQUEST\", \"message\": \"상대방의 유저 ID는 필수입니다.\", \"data\": null }"
                                    ),
                                    @ExampleObject(
                                            name = "동시성 오류",
                                            value = "{ \"code\": 400, \"status\": \"BAD_REQUEST\", \"message\": \"채팅방 생성에 실패했습니다. 잠시 후 다시 시도해주세요.\", \"data\": null }"
                                    )
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
    public ApiResponse<ChatRoomResponse> createChatRoom(
            @RequestBody ChatRoomCreateRequest request,
            @AuthenticationPrincipal CustomOAuth2User currentUser
    ) {
        ChatRoomResponse response = chatRoomService.createChatRoom(request.toServiceRequest(), currentUser.getMember());
        return ApiResponse.ok(response);
    }

    @GetMapping
    @Operation(
            summary = "사용자의 채팅방 목록 조회",
            description = """
                    현재 인증된 사용자가 참여 중인 채팅방 목록을 조회합니다.
                    각 채팅방 정보에 추가적으로 마지막 메시지 정보, 읽지 않은 채팅 개수, 채팅방에 참여중인 member 정보가 포함됩니다.
                    """
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "사용자의 채팅방 목록 조회 성공",
                    useReturnTypeSchema = true
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "유저 정보를 찾을 수 없는 경우",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = {
                                    @ExampleObject(
                                            name = "존재하지 않는 유저",
                                            value = "{ \"code\": 400, \"status\": \"BAD_REQUEST\", \"message\": \"해당 유저를 찾을 수 없습니다.\", \"data\": null }"
                                    )
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
    public ApiResponse<List<ChatRoomResponse>> getChatRooms(@AuthenticationPrincipal CustomOAuth2User currentUser) {
        List<ChatRoomResponse> chatRooms = chatRoomService.getChatRoomsForAuthenticatedMember(currentUser.getMember());
        return ApiResponse.ok(chatRooms);
    }
}