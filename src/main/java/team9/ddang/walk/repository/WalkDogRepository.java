package team9.ddang.walk.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import team9.ddang.dog.entity.Dog;
import team9.ddang.walk.entity.Walk;
import team9.ddang.walk.entity.WalkDog;

import java.util.List;

public interface WalkDogRepository extends JpaRepository<WalkDog, Long> {
    @EntityGraph(attributePaths = {"walk"})
    @Query("""
    SELECT wd.walk
    FROM WalkDog wd
    WHERE wd.dog = :dog
    """)
    List<Walk> findAllWalksByDog(Dog dog);

}
