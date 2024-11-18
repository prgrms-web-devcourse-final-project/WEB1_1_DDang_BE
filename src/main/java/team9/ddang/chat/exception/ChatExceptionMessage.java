package team9.ddang.chat.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ChatExceptionMessage {
    // Chat
    CHAT_NOT_FOUND("해당 채팅을 찾을 수 없습니다."),

    // ChatRoom
    CHATROOM_NOT_FOUND("해당 채팅방을 찾을 수 없습니다."),

    // ChatMember
    CHATMEMBER_NOT_IN_CHATROOM("해당 회원을 채팅방에서 찾을 수 없습니다.");

    private final String text;
}
