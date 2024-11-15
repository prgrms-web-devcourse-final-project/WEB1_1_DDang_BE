package team9.ddang.notification.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import team9.ddang.global.entity.BaseEntity;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class NotificationSettings extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long notificationSettingId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private IsAgreed friendRequestNotification;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private IsAgreed walkNotification;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private IsAgreed messageNotification;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private IsAgreed systemNotification;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private IsAgreed otherNotification;

    @Builder
    private NotificationSettings(IsAgreed friendRequestNotification, IsAgreed walkNotification, IsAgreed messageNotification, IsAgreed systemNotification, IsAgreed otherNotification) {
        this.friendRequestNotification = friendRequestNotification;
        this.walkNotification = walkNotification;
        this.messageNotification = messageNotification;
        this.systemNotification = systemNotification;
        this.otherNotification = otherNotification;
    }
}
// TODO : 알림 설정 추가?