package team9.ddang.notification.service.response;

import team9.ddang.notification.entity.IsRead;
import team9.ddang.notification.entity.Notification;
import team9.ddang.notification.entity.Type;

public record NotificationResponse(
        Long notificationId,
        Type type,
        String content,
        IsRead isRead,
        Long memberId
) {
    public static NotificationResponse of(Notification notification) {
        return new NotificationResponse(
                notification.getNotificationId(),
                notification.getType(),
                notification.getContent(),
                notification.getIsRead(),
                notification.getMember().getMemberId()
        );
    }
}
