package team9.ddang.chat.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import team9.ddang.chat.entity.Chat;

import java.util.List;
import java.util.Optional;

public interface ChatRepository extends JpaRepository<Chat, Long> {
    @Query("SELECT c FROM Chat c WHERE c.isDeleted = 'false' AND c.chatId = :id")
    Optional<Chat> findActiveById(Long id);

    @Query("""
                SELECT c 
                FROM Chat c 
                JOIN FETCH c.member 
                JOIN FETCH c.chatRoom 
                WHERE c.isDeleted = 'false' AND c.chatRoom.chatroomId = :chatRoomId
            """)
    Slice<Chat> findByChatRoomId(Long chatRoomId, Pageable pageable);

    @Query("SELECT c.text FROM Chat c " +
            "WHERE c.chatRoom.chatroomId = :chatRoomId " +
            "AND c.isDeleted = 'FALSE' " +
            "ORDER BY c.createdAt DESC LIMIT 1")
    String findLastMessageByChatRoom(@Param("chatRoomId") Long chatRoomId);

    @Query("""
                SELECT COUNT(c)
                FROM Chat c
                WHERE c.chatRoom.chatroomId = :chatRoomId
                  AND c.member.memberId <> :memberId
                  AND c.isRead = 'FALSE'
            """)
    Long countUnreadMessagesByChatRoomAndMember(@Param("chatRoomId") Long chatRoomId, @Param("memberId") Long memberId);

    @Query("""
        SELECT c
        FROM Chat c
        WHERE c.chatRoom.chatroomId = :chatRoomId
          AND c.member.memberId <> :memberId
          AND c.isRead = 'FALSE'
    """)
    List<Chat> findUnreadMessagesByChatRoomIdAndMemberId(@Param("chatRoomId") Long chatRoomId, @Param("memberId") Long memberId);
}