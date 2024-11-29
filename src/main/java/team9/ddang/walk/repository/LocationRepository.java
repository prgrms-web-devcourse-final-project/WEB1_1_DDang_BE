package team9.ddang.walk.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import team9.ddang.walk.entity.Location;
import team9.ddang.walk.entity.Position;

import java.util.List;

public interface LocationRepository extends JpaRepository<Location, Long> {

    @Query("""
    SELECT l.position
    FROM Location l
    WHERE l.walk.walkId = :walkId
    """)
    List<Position> findAllPositionByWalkId(Long walkId);
}
