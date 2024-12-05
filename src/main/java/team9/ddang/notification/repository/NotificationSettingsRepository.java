package team9.ddang.notification.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import team9.ddang.member.entity.Member;
import team9.ddang.notification.entity.NotificationSettings;
import team9.ddang.notification.entity.Type;

import java.util.Optional;

public interface NotificationSettingsRepository extends JpaRepository<NotificationSettings, Long> {

    Optional<NotificationSettings> findByMember_MemberIdAndType(Long memberId, Type type);

    @Modifying
    @Query("""
            UPDATE NotificationSettings ns
            SET ns.isDeleted = 'TRUE'
            WHERE ns.member.memberId = :memberId
            """)
    void deleteByMember(Long memberId);
}
