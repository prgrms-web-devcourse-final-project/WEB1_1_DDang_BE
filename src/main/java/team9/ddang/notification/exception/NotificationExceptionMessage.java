package team9.ddang.notification.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum NotificationExceptionMessage {

    NOTIFICATION_SETTINGS_NOT_FOUND("존재하지 않는 알림 설정입니다."),
    MEMBER_NOT_FOUND("존재하지 않는 회원입니다.");

    private final String text;
}
