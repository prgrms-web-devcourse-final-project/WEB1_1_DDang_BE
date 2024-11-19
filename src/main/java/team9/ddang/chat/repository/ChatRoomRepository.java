package team9.ddang.chat.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import team9.ddang.chat.entity.ChatRoom;
import team9.ddang.member.entity.Member;

import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    @Query("SELECT c FROM ChatRoom c WHERE c.isDeleted = 'false' AND c.chatroomId = :id")
    Optional<ChatRoom> findActiveById(Long id);

    @Query("SELECT c FROM ChatRoom c " +
            "WHERE c.isDeleted = 'FALSE' " +
            "AND EXISTS (SELECT cm FROM ChatMember cm WHERE cm.chatRoom = c AND cm.member = :member1 AND cm.isDeleted = 'FALSE') " +
            "AND EXISTS (SELECT cm FROM ChatMember cm WHERE cm.chatRoom = c AND cm.member = :member2 AND cm.isDeleted = 'FALSE')")
    Optional<ChatRoom> findOneToOneChatRoom(@Param("member1") Member member1, @Param("member2") Member member2);
}
