package team9.ddang.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import team9.ddang.member.entity.Friend;
import team9.ddang.member.entity.Member;

import java.util.List;

public interface FriendRepository  extends JpaRepository<Friend, Long> {

    @Query("""
    SELECT f.receiver
    FROM Friend f
    WHERE f.sender = :sender
    and f.isDeleted = 'FALSE'
    and f.receiver.isDeleted = 'FALSE'
    """)
    List<Member> findAllFriendsBySender(Member sender);

    @Modifying
    @Query("""
    DELETE FROM Friend f
    WHERE (f.sender = :member AND f.receiver = :otherMember)
       OR (f.sender = :otherMember AND f.receiver = :member)
""")
    void deleteBySenderAndReceiver(Member member, Member otherMember);

    boolean existsBySenderAndReceiver(Member sender, Member receiver);
}
