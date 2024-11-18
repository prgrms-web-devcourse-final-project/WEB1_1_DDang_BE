package team9.ddang.chat.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import team9.ddang.chat.entity.Chat;

import java.util.Optional;

public interface ChatRepository extends JpaRepository<Chat, Long> {
    @Query("SELECT c FROM Chat c WHERE c.isDeleted = 'false' AND c.chatId = :id")
    Optional<Chat> findActiveById(Long id);

    @Query("SELECT c FROM Chat c WHERE c.isDeleted = 'false' AND c.chatRoom.chatroomId = :chatRoomId")
    Slice<Chat> findByChatRoomId(Long chatRoomId, Pageable pageable);
}