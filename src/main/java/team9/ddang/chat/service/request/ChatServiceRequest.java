package team9.ddang.chat.service.request;

public record ChatServiceRequest(
        Long memberId,
        Long chatRoomId,
        String message) {
}
