package team9.ddang.chat.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team9.ddang.chat.entity.Chat;
import team9.ddang.chat.entity.ChatRoom;
import team9.ddang.chat.entity.ChatType;
import team9.ddang.chat.exception.ChatExceptionMessage;
import team9.ddang.chat.repository.ChatRepository;
import team9.ddang.chat.repository.ChatRoomRepository;
import team9.ddang.chat.service.response.ChatResponse;
import team9.ddang.member.entity.Member;
import team9.ddang.member.repository.MemberRepository;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

    private final ChatRepository chatRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final MemberRepository memberRepository;

    @Override
    @Transactional
    public ChatResponse saveChat(Long chatRoomId, Long memberId, String message) {

        ChatRoom chatRoom = findChatRoomByIdOrThrowException(chatRoomId);

        // TODO 나중에는 SpringSequrity에서 맴버 객체 받아서 사용할 예정
        Member member = memberRepository.findById(memberId).orElseThrow();

        Chat chat = Chat.builder()
                .chatRoom(chatRoom)
                .member(member)
                .chatType(ChatType.TALK)
                .text(message)
                .build();


        return new ChatResponse(chatRepository.save(chat));
    }

    @Override
    @Transactional(readOnly = true)
    public Slice<ChatResponse> findChatsByRoom(Long chatRoomId, Pageable pageable) {
        // TODO 나중에는 SpringSequrity에서 맴버 객체 받아서 사용할 예정
        // TODO 나중에 Member가 해당 채팅방에 속해있는지 검증 필요
        findChatRoomByIdOrThrowException(chatRoomId);
        Slice<Chat> chats = chatRepository.findByChatRoomId(chatRoomId, pageable);

        return chats.map(ChatResponse::new);
    }

    @Transactional
    public void markMessagesAsRead(Long chatRoomId, Long memberId) {
        // TODO 나중에는 SpringSequrity에서 맴버 객체 받아서 사용할 예정
        List<Chat> unreadChats = chatRepository.findUnreadMessagesByChatRoomIdAndMemberId(chatRoomId, memberId);
        unreadChats.forEach(Chat::markAsRead);
    }


    private ChatRoom findChatRoomByIdOrThrowException(Long id) {
        return chatRoomRepository.findActiveById(id)
                .orElseThrow(() -> {
                    log.warn(">>>> {} : {} <<<<", id, ChatExceptionMessage.CHATROOM_NOT_FOUND);
                    return new IllegalArgumentException(ChatExceptionMessage.CHATROOM_NOT_FOUND.getText());
                });
    }
}
