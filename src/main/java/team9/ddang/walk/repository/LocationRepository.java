package team9.ddang.walk.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import team9.ddang.walk.entity.Location;

public interface LocationRepository extends JpaRepository<Location, Long> {
}
