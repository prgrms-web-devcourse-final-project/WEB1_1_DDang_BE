package team9.ddang.family.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import team9.ddang.family.entity.DayOfWeek;
import team9.ddang.family.entity.WalkSchedule;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface WalkScheduleRepository extends JpaRepository<WalkSchedule, Long> {

    @Query("""
        SELECT w 
        FROM WalkSchedule w 
        WHERE w.family.familyId = :familyId 
          AND w.isDeleted = 'FALSE'
    """)
    List<WalkSchedule> findAllByFamilyId(@Param("familyId") Long familyId);

    @Query("""
        SELECT w 
        FROM WalkSchedule w 
        WHERE w.walkScheduleId = :id 
          AND w.isDeleted = 'FALSE'
    """)
    Optional<WalkSchedule> findActiveById(@Param("id") Long id);

    @EntityGraph(attributePaths = {"member"})
    @Query("""
        SELECT w 
        FROM WalkSchedule w 
        WHERE w.dog.dogId = :dogId 
          AND w.isDeleted = 'FALSE'
    """)
    List<WalkSchedule> findAllByDogId(@Param("dogId") Long dogId);

    @Modifying
    @Query("""
        DELETE FROM WalkSchedule w 
        WHERE w.walkScheduleId = :id
    """)
    void deleteById(@Param("id") Long id);

    @Modifying
    @Query("""
        DELETE FROM WalkSchedule w 
        WHERE w.member.memberId = :memberId
    """)
    void deleteByMemberId(@Param("memberId") Long memberId);

    @Modifying
    @Query("""
        DELETE FROM WalkSchedule w 
        WHERE w.family.familyId = :familyId
    """)
    void deleteByFamilyId(@Param("familyId") Long familyId);

    List<WalkSchedule> findByDayOfWeekAndWalkTime(DayOfWeek dayOfWeek, LocalTime walkTime);
  
    @Query("""
    SELECT w 
    FROM WalkSchedule w
    JOIN FETCH w.member
    JOIN FETCH w.dog
    WHERE w.family.familyId = :familyId AND w.isDeleted = 'FALSE'
""")
    List<WalkSchedule> findAllByFamilyIdWithDetails(@Param("familyId") Long familyId);

    @Modifying
    @Query("""
        DELETE FROM WalkSchedule w 
        WHERE w.dog.dogId = :dogId
    """)
    void deleteByDogId(@Param("dogId") Long dogId);

    @Query("""
    SELECT w 
    FROM WalkSchedule w
    WHERE w.member.memberId = :memberId AND w.isDeleted = 'FALSE'
""")
    List<WalkSchedule> findAllByMemberId(@Param("memberId") Long memberId);

    @Modifying
    @Query("""
        DELETE 
        FROM WalkSchedule ws 
        WHERE ws.walkScheduleId IN :ids
    """)
    void deleteAllById(@Param("ids") List<Long> ids);
}