package team9.ddang.notification.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import team9.ddang.notification.entity.NotificationSettings;
import team9.ddang.notification.entity.Type;

import java.util.Optional;

public interface NotificationSettingsRepository extends JpaRepository<NotificationSettings, Long> {

    Optional<NotificationSettings> findByMember_MemberIdAndType(Long memberId, Type type);
}
