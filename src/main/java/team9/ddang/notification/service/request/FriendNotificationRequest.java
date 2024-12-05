package team9.ddang.notification.service.request;

import team9.ddang.member.entity.Member;
import team9.ddang.notification.entity.IsRead;
import team9.ddang.notification.entity.Notification;
import team9.ddang.notification.entity.Type;

public record FriendNotificationRequest(Member sender, Member receiver) {

    public Notification toEntity() {
        String content = sender.getName() + "님과 친구가 되었습니다!";
        return Notification.builder()
                .type(Type.FRIEND)
                .content(content)
                .isRead(IsRead.FALSE)
                .member(receiver)
                .build();
    }
}
