package team9.ddang.walk.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import team9.ddang.member.entity.Member;
import team9.ddang.walk.entity.Walk;

import java.time.LocalDate;
import java.util.List;

public interface WalkRepository extends JpaRepository<Walk, Long> {

    @Query("""
            SELECT COALESCE(SUM(w.totalDistance), 0)
            FROM Walk w
            WHERE w.member.memberId = :memberId
            """)
    int findTotalDistanceByMemberId(@Param("memberId") Long memberId);

    @Query("""
            SELECT COALESCE(COUNT(w), 0) 
            FROM Walk w 
            WHERE w.member.memberId = :memberId
            """)
    int countWalksByMemberId(@Param("memberId") Long memberId);

    @Query(value = """
        SELECT * 
        FROM walk 
        WHERE member_id = :memberId 
        AND DATE(created_at) = :date
       """, nativeQuery = true)
    List<Walk> findAllByMemberAndDate(@Param("memberId") Long memberId, @Param("date") LocalDate date);

    @EntityGraph(attributePaths = {"member"})
    @Query(value = """
        SELECT w
        FROM Walk w
        WHERE w.member IN :members
        AND YEAR(w.createdAt) = :year
       """)
    List<Walk> findAllByMembersAndDate(@Param("members") List<Member> members, @Param("year") int year);


}
