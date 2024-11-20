package team9.ddang.chat.service.response;

import team9.ddang.chat.entity.ChatRoom;
import team9.ddang.member.entity.Member;

import java.util.List;
import java.util.stream.Collectors;

public record ChatRoomResponse(
        // TODO 나중에 채팅방 목록에 채팅방에 참여중인 인원에 대한 정보도 같이 반환하도록
        Long chatRoomId,
        String name,
        String lastMessage,
        Long unreadMessageCount
//        List<MemberResponse> members
) {
    public ChatRoomResponse(ChatRoom chatRoom, String lastMessage, Long unreadMessageCount) {
        this(
                chatRoom.getChatroomId(),
                chatRoom.getName(),
                lastMessage,
                unreadMessageCount
//                members.stream()
//                        .map(MemberResponse::new)
//                        .collect(Collectors.toList())
        );
    }
}