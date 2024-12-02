package team9.ddang.chat.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
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
                    이미 동일한 맴버와의 채팅방이 있다면, 해당 채팅방을 반환합니다.
                    요청 본문에 상대방의 회원 ID(opponentMemberId)를 포함해야 합니다.
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
    public ApiResponse<List<ChatRoomResponse>> getChatRooms(@AuthenticationPrincipal CustomOAuth2User currentUser) {
        List<ChatRoomResponse> chatRooms = chatRoomService.getChatRoomsForAuthenticatedMember(currentUser.getMember());
        return ApiResponse.ok(chatRooms);
    }
}