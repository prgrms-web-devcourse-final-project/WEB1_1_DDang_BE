package team9.ddang.dog.repository;

import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import team9.ddang.dog.entity.MemberDog;

import java.util.Optional;

public interface MemberDogRepository extends JpaRepository<MemberDog, Long> {

    @EntityGraph(attributePaths = {"dog"})
    @Query("select md from MemberDog md WHERE md.member.memberId = :memberId")
    Optional<MemberDog> findMemberDogByMemberId(Long memberId);

    @Query("SELECT md FROM MemberDog md WHERE md.dog.dogId = :dogId AND md.member.memberId = :memberId")
    Optional<MemberDog> findByDogIdAndMemberId(Long dogId, Long memberId);

    @Query("SELECT md FROM MemberDog md JOIN FETCH md.dog WHERE md.member.memberId = :memberId AND md.isDeleted = 'FALSE'")
    Optional<MemberDog> findOneByMemberIdAndNotDeleted(@Param("memberId") Long memberId);

}
