package team9.ddang.dog.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import team9.ddang.dog.entity.MemberDog;
import team9.ddang.member.entity.Member;

import java.util.Optional;

public interface MemberDogRepository extends JpaRepository<MemberDog, Long> {

    @EntityGraph(attributePaths = {"dog"})
    @Query("select md from MemberDog md WHERE md.member.memberId = :memberId")
    Optional<MemberDog> findMemberDogByMemberId(Long memberId);


    @Query("SELECT COUNT(md) > 0 FROM MemberDog md WHERE md.member = :member AND md.isDeleted = 'FALSE'")
    boolean existsByMember(Member member);
}
