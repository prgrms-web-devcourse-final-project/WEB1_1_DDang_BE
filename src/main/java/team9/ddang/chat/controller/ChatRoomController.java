package team9.ddang.chat.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import team9.ddang.chat.controller.request.ChatRoomCreateRequest;
import team9.ddang.chat.service.ChatRoomService;
import team9.ddang.chat.service.response.ChatRoomResponse;
import team9.ddang.global.api.ApiResponse;

import java.util.List;

@RestController
@RequestMapping("/api/v1/chat/rooms")
@RequiredArgsConstructor
public class ChatRoomController {

    private final ChatRoomService chatRoomService;

    @PostMapping
    public ApiResponse<ChatRoomResponse> createChatRoom(
            @RequestBody ChatRoomCreateRequest request
    ) {
        ChatRoomResponse response = chatRoomService.createChatRoom(request.opponentMemberId());
        return ApiResponse.ok(response);
    }

    @GetMapping
    public ApiResponse<List<ChatRoomResponse>> getChatRooms() {
        List<ChatRoomResponse> chatRooms = chatRoomService.getChatRoomsForAuthenticatedMember();
        return ApiResponse.ok(chatRooms);
    }
}