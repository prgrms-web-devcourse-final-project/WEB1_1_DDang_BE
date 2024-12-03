package team9.ddang.notification.service;

import team9.ddang.member.entity.Member;
import team9.ddang.notification.controller.request.NotificationSettingsRequest;
import team9.ddang.notification.service.response.SettingsResponse;
import team9.ddang.notification.service.response.SettingsUpdateResponse;

public interface NotificationSettingsService {

    void saveDefaultNotificationSettings(Member member);

    SettingsUpdateResponse updateSettings(Long memberId, NotificationSettingsRequest notificationSettingsRequest);

    SettingsResponse getSettings(Long memberId);
}
