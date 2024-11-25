package team9.ddang.dog.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import team9.ddang.dog.entity.Dog;

public interface DogRepository extends JpaRepository<Dog, Long> {
}
