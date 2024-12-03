package team9.ddang.notification.service;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import team9.ddang.member.entity.Member;
import team9.ddang.notification.service.response.NotificationResponse;

public interface NotificationService {

    Slice<NotificationResponse> getNotificationList(Member member, Pageable pageable);
}
