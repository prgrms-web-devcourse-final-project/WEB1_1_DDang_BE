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
    @Query("SELECT w FROM WalkSchedule w WHERE w.family.familyId = :familyId AND w.isDeleted = 'FALSE'")
    List<WalkSchedule> findAllByFamilyId(@Param("familyId") Long familyId);

    @Query("SELECT w FROM WalkSchedule w WHERE w.walkScheduleId = :id AND w.isDeleted = 'FALSE'")
    Optional<WalkSchedule> findActiveById(@Param("id") Long id);

    @EntityGraph(attributePaths = {"member"})
    @Query("SELECT w FROM WalkSchedule w WHERE w.dog.dogId = :dogId AND w.isDeleted = 'FALSE'")
    List<WalkSchedule> findAllByDogId(@Param("dogId") Long dogId);

    @Modifying
    @Query("UPDATE WalkSchedule w SET w.isDeleted = 'TRUE' WHERE w.walkScheduleId = :id")
    void softDeleteById(@Param("id") Long id);

    @Modifying
    @Query("UPDATE WalkSchedule w SET w.isDeleted = 'TRUE' WHERE w.member.memberId = :memberId")
    void softDeleteByMemberId(@Param("memberId") Long memberId);

    @Modifying
    @Query("UPDATE WalkSchedule w SET w.isDeleted = 'TRUE' WHERE w.family.familyId = :familyId")
    void softDeleteByFamilyId(@Param("familyId") Long familyId);

    List<WalkSchedule> findByDayOfWeekAndWalkTime(DayOfWeek dayOfWeek, LocalTime walkTime);
}