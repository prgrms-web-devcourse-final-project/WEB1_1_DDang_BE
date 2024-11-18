package team9.ddang.chat.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import team9.ddang.chat.entity.ChatMember;

import java.util.Optional;

public interface ChatMemberRepository extends JpaRepository<ChatMember, Long> {
    @Query("SELECT c FROM ChatMember c WHERE c.isDeleted = 'false' AND c.chatMemberId = :id")
    Optional<ChatMember> findActiveById(Long id);
}
