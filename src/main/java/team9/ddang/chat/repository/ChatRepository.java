package team9.ddang.chat.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import team9.ddang.chat.entity.Chat;

import java.time.LocalDateTime;
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

    @Query("""
        SELECT cm.chatRoom.chatroomId, COUNT(c)
        FROM ChatMember cm
        LEFT JOIN Chat c ON cm.chatRoom.chatroomId = c.chatRoom.chatroomId
        WHERE cm.member.email = :email
          AND cm.isDeleted = 'FALSE'
          AND (c.isRead = 'FALSE' OR c.isRead IS NULL)
        GROUP BY cm.chatRoom.chatroomId
       """)
    List<Object[]> countUnreadMessagesByMemberEmail(@Param("email") String email);

    @Query("""
    SELECT c
    FROM Chat c
    WHERE c.chatRoom.chatroomId = :chatRoomId
      AND c.isDeleted = 'FALSE'
      AND c.createdAt < :lastMessageCreatedAt
""")
    Slice<Chat> findChatsBefore(@Param("chatRoomId") Long chatRoomId, @Param("lastMessageCreatedAt") LocalDateTime lastMessageCreatedAt, Pageable pageable);

    @Modifying
    @Query("DELETE FROM Chat c WHERE c.createdAt < :twoWeeksAgo")
    void deleteChatsOlderThan(LocalDateTime twoWeeksAgo);
}