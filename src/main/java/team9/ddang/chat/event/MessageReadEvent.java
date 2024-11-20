package team9.ddang.chat.event;

import java.util.List;

public record MessageReadEvent(
        Long chatRoomId,
        Long memberId,
        List<Long> readMessageIds
) {}
