package team9.ddang.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import team9.ddang.member.entity.Member;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    @Query("SELECT m FROM Member m WHERE m.email = :email AND m.isDeleted = 'FALSE'")
    Optional<Member> findByEmail(@Param("email") String email);

    @Query("SELECT m FROM Member m WHERE m.memberId = :id AND m.isDeleted = 'FALSE'")
    Optional<Member> findActiveById(@Param("id") Long id);

    @Query("SELECT m FROM Member m WHERE m.family.familyId = :familyId AND m.isDeleted = 'FALSE'")
    List<Member> findAllByFamilyId(@Param("familyId") Long familyId);

    @Query("""
    SELECT m 
    FROM Member m 
    WHERE m.family.familyId =
    (SELECT m2.family.familyId FROM Member m2 WHERE m2.memberId = :memberId)
""")
    List<Member> findFamilyMembersByMemberId(@Param("memberId") Long memberId);

    @Modifying
    @Query("""
            UPDATE Member m
            SET m.isDeleted = 'TRUE'
            WHERE m.memberId = :memberId
            """)
    void softDeleteById(@Param("memberId") Long memberId);
}
