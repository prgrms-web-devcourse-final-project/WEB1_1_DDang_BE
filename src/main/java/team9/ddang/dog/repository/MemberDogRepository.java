package team9.ddang.dog.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import team9.ddang.dog.entity.MemberDog;
import team9.ddang.walk.service.response.MemberNearbyResponse;

import java.util.List;
import java.util.Optional;

public interface MemberDogRepository extends JpaRepository<MemberDog, Long> {

    @EntityGraph(attributePaths = {"dog"})
    @Query("SELECT md " +
            "FROM MemberDog md " +
            "WHERE md.member.memberId = :memberId")
    Optional<MemberDog> findMemberDogByMemberId(Long memberId);

    @Query("SELECT new team9.ddang.walk.service.response.MemberNearbyResponse(d.dogId, d.profileImg, d.name, d.breed, d.walkCount, m.memberId, m.birthDate, m.gender) " +
            "FROM MemberDog md " +
            "JOIN md.dog d " +
            "JOIN md.member m " +
            "WHERE md.member.email " +
            "IN :emails")
    List<MemberNearbyResponse> findDogsAndMembersByMemberEmails(@Param("emails") List<String> emails);

}
