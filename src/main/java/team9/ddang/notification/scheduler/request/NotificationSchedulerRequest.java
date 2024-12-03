package team9.ddang.notification.scheduler.request;

import team9.ddang.notification.entity.Notification;

public record NotificationSchedulerRequest(
        Long notificationId,
        String content,
        String memberEmail
) {
    public static NotificationSchedulerRequest of(Notification notification) {
        return new NotificationSchedulerRequest(
                notification.getNotificationId(),
                notification.getContent(),
                notification.getMember().getEmail()
        );
    }
}

