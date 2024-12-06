package team9.ddang.family.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import team9.ddang.family.entity.Family;

import java.util.Optional;

public interface FamilyRepository extends JpaRepository<Family, Long> {

    @Query("""
        SELECT f 
        FROM Family f 
        WHERE f.familyId = :id 
          AND f.isDeleted = 'FALSE'
    """)
    Optional<Family> findActiveById(@Param("id") Long id);

    @Modifying
    @Query("""
        UPDATE Family f 
        SET f.isDeleted = 'TRUE' 
        WHERE f.familyId = :familyId
    """)
    void softDeleteFamilyById(@Param("familyId") Long familyId);
}
