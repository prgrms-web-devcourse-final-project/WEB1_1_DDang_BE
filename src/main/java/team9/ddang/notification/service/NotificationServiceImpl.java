package team9.ddang.notification.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team9.ddang.member.entity.Member;
import team9.ddang.notification.entity.Notification;
import team9.ddang.notification.repository.NotificationRepository;
import team9.ddang.notification.service.response.NotificationResponse;

@Service
@RequiredArgsConstructor
@Transactional
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;

    @Transactional(readOnly = true)
    @Override
    public Slice<NotificationResponse> getNotificationList(Member member, Pageable pageable) {
        Slice<Notification> notifications = notificationRepository.findAllByMember(member, pageable);
        return notifications.map(NotificationResponse::of);
    }
}
