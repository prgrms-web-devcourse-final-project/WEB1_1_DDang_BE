package team9.ddang.chat.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import team9.ddang.chat.entity.Chat;
import team9.ddang.chat.repository.ChatRepository;

@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

    private final ChatRepository chatRepository;

    @Override
    public Chat saveChat(Chat chat) {
        return chatRepository.save(chat);
    }
}
