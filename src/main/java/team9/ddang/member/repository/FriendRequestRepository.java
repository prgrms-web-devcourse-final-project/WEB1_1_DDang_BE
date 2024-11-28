package team9.ddang.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import team9.ddang.member.entity.FriendRequest;
import team9.ddang.member.entity.Member;

public interface FriendRequestRepository extends JpaRepository<FriendRequest, Long> {

    boolean existsFriendRequestByReceiver(Member receiver);

    void deleteByReceiver(Member receiver);
}