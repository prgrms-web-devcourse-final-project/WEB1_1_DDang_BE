package team9.ddang.walk.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import team9.ddang.walk.entity.Walk;

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
}
