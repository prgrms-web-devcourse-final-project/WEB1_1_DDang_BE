package team9.ddang.notification.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import team9.ddang.global.entity.BaseEntity;
import team9.ddang.member.entity.Member;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class NotificationSettings extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long notificationSettingId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Type type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private IsAgreed isAgreed;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Builder
    private NotificationSettings(Type type, IsAgreed isAgreed, Member member) {
        this.type = type;
        this.isAgreed = isAgreed;
        this.member = member;
    }

    public void updateIsAgreed(IsAgreed isAgreed) {
        this.isAgreed = isAgreed;
    }
}