package team9.ddang.walk.repository;

import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import team9.ddang.walk.entity.WalkDog;

import java.util.List;

public interface WalkDogRepository extends JpaRepository<WalkDog, Long> {

    List<WalkDog> findAllByDog_DogId(Long dogId);

    @Query("SELECT wd FROM WalkDog wd WHERE YEAR(wd.createdAt) = :year AND wd.dog.dogId = :dogId")
    List<WalkDog> findWalkDogsByYearAndDogId(@Param("year") int year, @Param("dogId") Long dogId);


}
