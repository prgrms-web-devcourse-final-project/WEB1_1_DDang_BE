package team9.ddang.chat.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import team9.ddang.chat.entity.Chat;
import team9.ddang.chat.entity.ChatRoom;
import team9.ddang.chat.entity.ChatType;
import team9.ddang.chat.exception.ChatExceptionMessage;
import team9.ddang.chat.repository.ChatRepository;
import team9.ddang.chat.repository.ChatRoomRepository;
import team9.ddang.member.entity.Member;
import team9.ddang.member.repository.MemberRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

    private final ChatRepository chatRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final MemberRepository memberRepository;

    @Override
    public Chat saveChat(Long chatRoomId, Long memberId, String message) {

        // TODO 나중에는 ChatRoomID 검색해서 유효성 확인할 예정
        ChatRoom chatRoom = findChatRoomByIdOrThrowException(chatRoomId);

        // TODO 나중에는 SpringSequrity에서 맴버 객체 받아서 사용할 예정
        Member member = memberRepository.findById(memberId).orElseThrow();

        Chat chat = Chat.builder()
                .chatRoom(chatRoom)
                .member(member)
                .chatType(ChatType.TALK)
                .text(message)
                .build();

        return chatRepository.save(chat);
    }


    private ChatRoom findChatRoomByIdOrThrowException(Long id) {
        return chatRoomRepository.findActiveById(id)
                .orElseThrow(() -> {
                    log.warn(">>>> {} : {} <<<<", id, ChatExceptionMessage.CHATROOM_NOT_FOUND);
                    return new IllegalArgumentException(ChatExceptionMessage.CHATROOM_NOT_FOUND.getText());
                });
    }
}
