package team9.ddang.walk.repository;

import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import team9.ddang.walk.entity.Walk;
import team9.ddang.walk.entity.WalkDog;
import java.time.LocalDateTime;
import java.util.List;

public interface WalkDogRepository extends JpaRepository<WalkDog, Long> {
  
   @EntityGraph(attributePaths = {"walk"})
    @Query("SELECT wd.walk FROM WalkDog wd JOIN wd.walk w WHERE wd.dog.dogId = :dogId AND w.createdAt BETWEEN :startOfDay AND :endOfDay")
    List<Walk> findWalksByDogIdAndToday(@Param("dogId") Long dogId, @Param("startOfDay") LocalDateTime startOfDay, @Param("endOfDay") LocalDateTime endOfDay);

   @Query("""
    SELECT wd
    FROM WalkDog wd
    WHERE wd.dog.dogId = :dogId
    AND wd.dog.isDeleted = 'FALSE'
""")
    List<WalkDog> findAllByDog_DogId(Long dogId);

    @Query("SELECT wd FROM WalkDog wd WHERE YEAR(wd.createdAt) = :year AND wd.dog.dogId = :dogId")
    List<WalkDog> findWalkDogsByYearAndDogId(@Param("year") int year, @Param("dogId") Long dogId);

    @Query("""
    SELECT wd.walk
    FROM WalkDog wd
    WHERE wd.dog.dogId =
    (SELECT md.dog.dogId FROM MemberDog md WHERE md.member.memberId = :memberId)
""")
    List<Walk> findWalksByDogIdFromMemberId(Long memberId);

    @Query("""
    SELECT wd.walk
    FROM WalkDog wd
    WHERE wd.dog.dogId =
    (SELECT md.dog.dogId FROM MemberDog md WHERE md.member.memberId = :memberId)
    And Month(wd.createdAt) = :month
""")
    List<Walk> findWalksByDogIdAndMonthFromMemberId(Long memberId, int month);

}
