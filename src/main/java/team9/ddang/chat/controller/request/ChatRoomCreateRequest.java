package team9.ddang.chat.controller.request;

import jakarta.validation.constraints.NotNull;

public record ChatRoomCreateRequest(
        @NotNull(message = "상대방의 회원 ID는 필수입니다.")
        Long opponentMemberId
) {}