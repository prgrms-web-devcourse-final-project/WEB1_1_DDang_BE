package team9.ddang.family.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import team9.ddang.family.entity.WalkSchedule;

public interface WalkScheduleRepository extends JpaRepository<WalkSchedule, Long> {
}