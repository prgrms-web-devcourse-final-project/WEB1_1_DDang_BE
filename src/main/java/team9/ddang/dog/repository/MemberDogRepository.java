package team9.ddang.dog.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import team9.ddang.dog.entity.MemberDog;

import java.util.Optional;

public interface MemberDogRepository extends JpaRepository<MemberDog, Long> {

    @EntityGraph(attributePaths = {"dog"})
    @Query("select md from MemberDog md WHERE md.member.memberId = :memberId")
    Optional<MemberDog> findMemberDogByMemberId(Long memberId);

}
