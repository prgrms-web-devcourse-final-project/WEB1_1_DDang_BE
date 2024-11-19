package team9.ddang.chat.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import team9.ddang.chat.entity.ChatMember;
import team9.ddang.chat.entity.ChatRoom;
import team9.ddang.member.entity.Member;

import java.util.List;
import java.util.Optional;

public interface ChatMemberRepository extends JpaRepository<ChatMember, Long> {
    @Query("SELECT c FROM ChatMember c WHERE c.isDeleted = 'false' AND c.chatMemberId = :id")
    Optional<ChatMember> findActiveById(Long id);

    @Query("SELECT cm.member FROM ChatMember cm " +
            "WHERE cm.chatRoom = :chatRoom AND cm.isDeleted = 'FALSE'")
    List<Member> findMembersByChatRoom(@Param("chatRoom") ChatRoom chatRoom);

    @Query("SELECT COUNT(cm) > 0 FROM ChatMember cm " +
            "WHERE cm.chatRoom.chatroomId = :chatRoomId AND cm.member.memberId = :memberId AND cm.isDeleted = 'FALSE'")
    boolean existsByChatRoomIdAndMemberId(@Param("chatRoomId") Long chatRoomId, @Param("memberId") Long memberId);
}
