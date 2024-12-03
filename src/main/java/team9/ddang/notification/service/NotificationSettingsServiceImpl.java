package team9.ddang.notification.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team9.ddang.member.entity.Member;
import team9.ddang.member.repository.MemberRepository;
import team9.ddang.notification.controller.request.NotificationSettingsRequest;
import team9.ddang.notification.entity.IsAgreed;
import team9.ddang.notification.entity.NotificationSettings;
import team9.ddang.notification.entity.Type;
import team9.ddang.notification.repository.NotificationSettingsRepository;
import team9.ddang.notification.service.response.SettingsResponse;
import team9.ddang.notification.service.response.SettingsUpdateResponse;

import static team9.ddang.notification.exception.NotificationExceptionMessage.MEMBER_NOT_FOUND;
import static team9.ddang.notification.exception.NotificationExceptionMessage.NOTIFICATION_SETTINGS_NOT_FOUND;

@Service
@RequiredArgsConstructor
@Transactional
public class NotificationSettingsServiceImpl implements NotificationSettingsService {

    private final NotificationSettingsRepository notificationSettingsRepository;
    private final MemberRepository memberRepository;

    @Override
    public void saveDefaultNotificationSettings(Member member) {
        NotificationSettings walkSettings = NotificationSettings.builder()
                .type(Type.WALK)
                .isAgreed(IsAgreed.TRUE)
                .member(member)
                .build();

        NotificationSettings chatSettings = NotificationSettings.builder()
                .type(Type.CHAT)
                .isAgreed(IsAgreed.TRUE)
                .member(member)
                .build();

        notificationSettingsRepository.save(walkSettings);
        notificationSettingsRepository.save(chatSettings);
    }

    @Transactional(readOnly = true)
    @Override
    public SettingsResponse getSettings(Long memberId) {

        NotificationSettings walkSettings = notificationSettingsRepository.findByMember_MemberIdAndType(memberId, Type.WALK)
                .orElseThrow(() -> new IllegalArgumentException(NOTIFICATION_SETTINGS_NOT_FOUND.getText()));

        NotificationSettings chatSettings = notificationSettingsRepository.findByMember_MemberIdAndType(memberId, Type.CHAT)
                .orElseThrow(() -> new IllegalArgumentException(NOTIFICATION_SETTINGS_NOT_FOUND.getText()));

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException(MEMBER_NOT_FOUND.getText()));

        return SettingsResponse.from(memberId, member.getIsMatched(), walkSettings, chatSettings);
    }

    @Override
    public SettingsUpdateResponse updateSettings(Long memberId, NotificationSettingsRequest notificationSettingsRequest) {

        NotificationSettings notificationSettings = notificationSettingsRepository.findByMember_MemberIdAndType(memberId, notificationSettingsRequest.type())
                .orElseThrow(() -> new IllegalArgumentException("해당 유저의 알림 설정이 존재하지 않습니다."));

        notificationSettings.updateIsAgreed(notificationSettingsRequest.isAgreed());

        return SettingsUpdateResponse.from(notificationSettings);
    }
}
