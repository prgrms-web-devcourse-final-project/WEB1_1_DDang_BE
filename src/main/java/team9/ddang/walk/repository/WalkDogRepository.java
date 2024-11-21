package team9.ddang.walk.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import team9.ddang.walk.entity.WalkDog;

public interface WalkDogRepository extends JpaRepository<WalkDog, Long> {
}
