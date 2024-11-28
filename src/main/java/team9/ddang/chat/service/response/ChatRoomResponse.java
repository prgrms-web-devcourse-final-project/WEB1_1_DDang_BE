package team9.ddang.chat.service.response;

import io.swagger.v3.oas.annotations.media.Schema;
import team9.ddang.chat.entity.ChatRoom;
import team9.ddang.member.entity.Member;

import java.util.List;
import java.util.stream.Collectors;

@Schema(description = "채팅방 응답 데이터")
public record ChatRoomResponse(
        // TODO 나중에 채팅방 목록에 채팅방에 참여중인 인원에 대한 정보도 같이 반환하도록
        @Schema(description = "채팅방 ID", example = "1")
        Long chatRoomId,
        @Schema(description = "채팅방 이름", example = "Team Chat")
        String name,
        @Schema(description = "마지막 메시지", example = "안녕하세요!")
        String lastMessage,
        @Schema(description = "읽지 않은 메시지 개수", example = "3")
        Long unreadMessageCount,
        @Schema(description = "채팅방 참여자")
        List<ChatMemberInfo> members
) {
    public ChatRoomResponse(ChatRoom chatRoom, String lastMessage, Long unreadMessageCount, List<Member> members) {
        this(
                chatRoom.getChatroomId(),
                chatRoom.getName(),
                lastMessage,
                unreadMessageCount,
                members.stream()
                        .map(ChatMemberInfo::new)
                        .collect(Collectors.toList())
        );
    }
}