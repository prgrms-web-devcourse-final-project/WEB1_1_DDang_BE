package team9.ddang.dog.repository;

import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import team9.ddang.dog.entity.Dog;

public interface DogRepository extends JpaRepository<Dog, Long> {
    @Modifying
    @Query("UPDATE Dog d SET d.isDeleted = 'TRUE' WHERE d.dogId = :dogId")
    void softDeleteById(@Param("dogId") Long dogId);
}
