package team9.ddang.chat.repository;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import team9.ddang.chat.entity.ChatRoom;
import team9.ddang.member.entity.Member;

import java.util.List;
import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    @Query("""
        SELECT c 
        FROM ChatRoom c 
        WHERE c.isDeleted = 'false' 
          AND c.chatroomId = :id
    """)
    Optional<ChatRoom> findActiveById(Long id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
        SELECT c 
        FROM ChatRoom c 
        WHERE c.isDeleted = 'FALSE' 
          AND EXISTS (
              SELECT cm 
              FROM ChatMember cm 
              WHERE cm.chatRoom = c 
                AND cm.member = :member1 
                AND cm.isDeleted = 'FALSE'
          ) 
          AND EXISTS (
              SELECT cm 
              FROM ChatMember cm 
              WHERE cm.chatRoom = c 
                AND cm.member = :member2 
                AND cm.isDeleted = 'FALSE'
          )
    """)
    Optional<ChatRoom> findOneToOneChatRoom(@Param("member1") Member member1, @Param("member2") Member member2);

    @Query("""
        SELECT DISTINCT c 
        FROM ChatRoom c 
        JOIN ChatMember cm ON cm.chatRoom = c 
        WHERE cm.member = :member 
          AND c.isDeleted = 'FALSE' 
          AND cm.isDeleted = 'FALSE'
    """)
    List<ChatRoom> findChatRoomsByMember(@Param("member") Member member);

    @Query("""
        SELECT c.chatroomId 
        FROM ChatRoom c 
        WHERE c.isDeleted = 'FALSE'
    """)
    List<Long> findAllActiveChatRoomIds();
}
