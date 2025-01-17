package team9.ddang.dog.repository;

import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import team9.ddang.dog.entity.Dog;

import java.util.List;
import java.util.Optional;

public interface DogRepository extends JpaRepository<Dog, Long> {
  
    @Modifying
    @Query("UPDATE Dog d SET d.isDeleted = 'TRUE' WHERE d.dogId = :dogId")
    void softDeleteById(@Param("dogId") Long dogId);
  
    @Query("SELECT d FROM Dog d WHERE d.dogId = :id AND d.isDeleted = 'FALSE'")
    Optional<Dog> findActiveById(@Param("id") Long id);

    @Query("SELECT d FROM Dog d WHERE d.family.familyId = :familyId AND d.isDeleted = 'FALSE'")
    List<Dog> findAllByFamilyId(@Param("familyId") Long familyId);

    @Query("SELECT d FROM Dog d WHERE d.family.familyId = :familyId AND d.isDeleted = 'FALSE'")
    Optional<Dog> findActiveByFamilyId(@Param("familyId") Long familyId);
}
