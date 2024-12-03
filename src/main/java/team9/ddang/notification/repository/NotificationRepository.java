package team9.ddang.notification.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import team9.ddang.member.entity.Member;
import team9.ddang.notification.entity.Notification;

import java.time.LocalDateTime;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    @Modifying
    @Query("""
            DELETE FROM Notification n
            WHERE n.createdAt < :deleteBeforeDate
            """)
    void deleteByCreatedAtBefore(LocalDateTime deleteBeforeDate);

    Slice<Notification> findAllByMember(Member member, Pageable pageable);
}