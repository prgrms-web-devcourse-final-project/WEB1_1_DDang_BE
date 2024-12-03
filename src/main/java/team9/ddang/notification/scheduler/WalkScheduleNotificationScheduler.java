package team9.ddang.notification.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import team9.ddang.family.entity.DayOfWeek;
import team9.ddang.family.entity.WalkSchedule;
import team9.ddang.family.repository.WalkScheduleRepository;
import team9.ddang.notification.entity.*;
import team9.ddang.notification.repository.NotificationRepository;
import team9.ddang.notification.repository.NotificationSettingsRepository;
import team9.ddang.global.api.WebSocketResponse;
import team9.ddang.notification.scheduler.request.NotificationSchedulerRequest;
import team9.ddang.notification.scheduler.request.FamilyRoleMessageRequest;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class WalkScheduleNotificationScheduler {

    private final WalkScheduleRepository walkScheduleRepository;
    private final NotificationSettingsRepository notificationSettingsRepository;
    private final NotificationRepository notificationRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @Scheduled(cron = "0 */5 * * * *") // 매 5분마다 실행
    @Transactional
    public void sendWalkScheduleNotifications() {
        log.info("산책 일정 조회 스케줄러 동작 시작...");

        java.time.DayOfWeek currentDay = LocalDateTime.now().getDayOfWeek();
        LocalTime currentTime = LocalTime.now().truncatedTo(ChronoUnit.SECONDS);;

        LocalTime targetTime = currentTime.plusMinutes(15);

        DayOfWeek customDayOfWeek = convertJavaDayOfWeekToCustomDayOfWeek(currentDay);
        log.info("Custom day: {}", customDayOfWeek);
        List<WalkSchedule> upcomingSchedules = walkScheduleRepository.findByDayOfWeekAndWalkTime(
                customDayOfWeek, targetTime
        );
        log.info("Upcoming schedules: {}", upcomingSchedules);

        // 조건에 맞는 알림을 저장 및 WebSocket 메시지 전송
        for (WalkSchedule schedule : upcomingSchedules) {
            saveNotificationAndSendMessage(schedule);
        }

        log.info("산책 일정 조회 스케줄러 동작 완료!");
    }

    private void saveNotificationAndSendMessage(WalkSchedule schedule) {
        // 알림 설정이 TRUE인 멤버 필터링
        NotificationSettings settings = notificationSettingsRepository.findByMember_MemberIdAndType(
                schedule.getMember().getMemberId(), Type.WALK
        ).orElse(null);

        if (settings != null && settings.getIsAgreed().equals(IsAgreed.TRUE)) {

            FamilyRoleMessageRequest familyRoleMessageRequest = FamilyRoleMessageRequest.from(schedule.getMember().getFamilyRole());
            String content = familyRoleMessageRequest.message() + " 곧 산책 갈 시간이에요!";

            // 알림 저장
            Notification notification = Notification.builder()
                    .type(Type.WALK)
                    .content(content)
                    .isRead(IsRead.FALSE)
                    .member(schedule.getMember())
                    .build();

            notification = notificationRepository.save(notification);

            // WebSocket 메시지 전송
            sendMessageToNotificationUrl(schedule.getMember().getEmail(), notification);
        }
    }

    private void sendMessageToNotificationUrl(String email, Notification notification) {
        NotificationSchedulerRequest request = NotificationSchedulerRequest.of(notification);
        messagingTemplate.convertAndSend("/sub/notification/" + email, WebSocketResponse.ok(request));
        log.info("Notification sent to WebSocket for email: {}", email);
    }

    private DayOfWeek convertJavaDayOfWeekToCustomDayOfWeek(java.time.DayOfWeek javaDayOfWeek) {
        return DayOfWeek.valueOf(javaDayOfWeek.name());
    }
}
