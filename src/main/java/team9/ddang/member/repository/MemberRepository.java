package team9.ddang.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import team9.ddang.family.entity.Family;
import team9.ddang.member.entity.Member;


import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByEmail(String email);

    @Query("SELECT m FROM Member m WHERE m.memberId = :id AND m.isDeleted = 'FALSE'")
    Optional<Member> findActiveById(@Param("id") Long id);
}
