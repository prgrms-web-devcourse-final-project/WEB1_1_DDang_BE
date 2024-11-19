package team9.ddang.chat.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import team9.ddang.chat.controller.request.ChatRoomCreateRequest;
import team9.ddang.chat.service.ChatRoomService;
import team9.ddang.chat.service.response.ChatRoomResponse;
import team9.ddang.global.api.ApiResponse;

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
}