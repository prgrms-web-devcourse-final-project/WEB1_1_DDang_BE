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

    @EntityGraph(attributePaths = {"member"})
    @Query(value = """
        SELECT w
        FROM Walk w 
        WHERE w.member IN :members 
        AND DATE(w.createdAt) = :date
       """)
    List<Walk> findAllByMembersAndDate(@Param("members") List<Member> members, @Param("date") LocalDate date);

    @EntityGraph(attributePaths = {"member"})
    @Query(value = """
        SELECT w
        FROM Walk w
        WHERE w.member IN :members
        AND YEAR(w.createdAt) = :year
       """)
    List<Walk> findAllByMembersAndYear(@Param("members") List<Member> members, @Param("year") int year);

    @Query("""
        SELECT COALESCE(SUM(w.totalDistance), 0)
        FROM Walk w
        WHERE w.member.family.familyId = :familyId
        """)
    int findTotalDistanceByFamilyId(@Param("familyId") Long familyId);

    @Query("""
        SELECT COALESCE(COUNT(w), 0)
        FROM Walk w
        WHERE w.member.family.familyId = :familyId
        """)
    int countWalksByFamilyId(@Param("familyId") Long familyId);
}
