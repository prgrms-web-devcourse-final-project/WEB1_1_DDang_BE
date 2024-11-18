package team9.ddang.chat.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import team9.ddang.chat.entity.Chat;

public interface ChatRepository extends JpaRepository<Chat, Long> {
}