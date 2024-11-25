package team9.ddang.walk.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import team9.ddang.walk.entity.Walk;

public interface WalkRepository extends JpaRepository<Walk, Long> {
}
