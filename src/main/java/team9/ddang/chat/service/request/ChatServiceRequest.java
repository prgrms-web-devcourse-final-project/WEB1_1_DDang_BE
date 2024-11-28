package team9.ddang.chat.service.request;

public record ChatServiceRequest(
        Long chatRoomId,
        String message) {
}
