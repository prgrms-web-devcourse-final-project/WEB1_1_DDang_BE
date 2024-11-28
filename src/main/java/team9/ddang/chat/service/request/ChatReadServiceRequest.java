package team9.ddang.chat.service.request;

import java.util.List;

public record ChatReadServiceRequest(
        Long chatRoomId,
        String email,
        List<Long> readMessageIds
) {
}
