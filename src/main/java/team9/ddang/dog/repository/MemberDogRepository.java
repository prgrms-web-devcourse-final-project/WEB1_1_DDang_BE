package team9.ddang.dog.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import team9.ddang.dog.entity.MemberDog;
import team9.ddang.member.entity.Member;
import team9.ddang.walk.service.response.walk.MemberNearbyInfo;

import java.util.List;
import java.util.Optional;

public interface MemberDogRepository extends JpaRepository<MemberDog, Long> {

    @EntityGraph(attributePaths = {"dog"})
    @Query("""
            SELECT md
            FROM MemberDog md 
            WHERE md.member.memberId = :memberId
            """)
    Optional<MemberDog> findMemberDogByMemberId(Long memberId);



    @Query("SELECT COUNT(md) > 0 FROM MemberDog md WHERE md.member = :member AND md.isDeleted = 'FALSE'")
    boolean existsByMember(Member member);

    @Query("SELECT md FROM MemberDog md WHERE md.member = :member AND md.isDeleted = 'FALSE'")
    List<MemberDog> findAllByMember(@Param("member") Member member);

    @Modifying
    @Query("UPDATE MemberDog md SET md.isDeleted = 'TRUE' WHERE md.member = :member")
    void softDeleteByMember(@Param("member") Member member);
           
    @Query("""
            SELECT new team9.ddang.walk.service.response.walk.MemberNearbyInfo
            (d.dogId, d.breed, d.name,d.profileImg, d.walkCount, m.memberId, d.birthDate, d.gender, m.isMatched, m.email) 
            FROM MemberDog md  
            JOIN md.dog d  
            JOIN md.member m  
            WHERE md.member.email  
            IN :emails
            """)
    List<MemberNearbyInfo> findDogsAndMembersByMemberEmails(@Param("emails") List<String> emails);

    @EntityGraph(attributePaths = {"dog", "member"})
    @Query("""
            SELECT md
            FROM MemberDog md
            WHERE md.member.email = :email
            """)
    Optional<MemberDog> findMemberDogByMemberEmail(@Param("email") String email);
}
