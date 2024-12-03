package team9.ddang.notification.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import team9.ddang.notification.repository.NotificationRepository;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationDeleteScheduler {

    private final NotificationRepository notificationRepository;

    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void deleteExpiredNotifications() {
        log.info("만료된 알림 삭제 스케줄러 동작 시작...");

        LocalDateTime currentDateTime = LocalDateTime.now();
        LocalDateTime deleteBeforeDate = currentDateTime.minusDays(5);

        notificationRepository.deleteByCreatedAtBefore(deleteBeforeDate);  // 5일 이상 지난 알림 삭제

        log.info("만료된 알림 삭제 스케줄러 동작 완료!");
    }
}