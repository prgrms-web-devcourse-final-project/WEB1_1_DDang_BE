package team9.ddang.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import team9.ddang.member.entity.WalkWithMember;

public interface WalkWithMemberRepository extends JpaRepository<WalkWithMember, Long> {

    @Query("""
            SELECT COALESCE(COUNT(w), 0) 
            FROM WalkWithMember w 
            WHERE w.sender.memberId = :memberId AND w.isDeleted = 'FALSE'
            """)
    int countBySenderMemberId(@Param("memberId") Long memberId);
}
