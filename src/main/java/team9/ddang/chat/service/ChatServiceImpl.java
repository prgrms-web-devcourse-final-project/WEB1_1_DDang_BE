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
import team9.ddang.chat.event.MessageReadEvent;
import team9.ddang.chat.exception.ChatExceptionMessage;
import team9.ddang.chat.producer.ChatProducer;
import team9.ddang.chat.repository.ChatMemberRepository;
import team9.ddang.chat.repository.ChatRepository;
import team9.ddang.chat.repository.ChatRoomRepository;
import team9.ddang.chat.service.request.ChatServiceRequest;
import team9.ddang.chat.service.response.ChatResponse;
import team9.ddang.family.exception.FamilyExceptionMessage;
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
    private final ChatProducer chatProducer;
    private final ChatMemberRepository chatMemberRepository;

    @Override
    @Transactional
    public ChatResponse saveChat(Long chatRoomId, String email, String message) {

        ChatRoom chatRoom = findChatRoomByIdOrThrowException(chatRoomId);

        Member member = findMemberByEmailOrThrowException(email);

        Chat chat = Chat.builder()
                .chatRoom(chatRoom)
                .member(member)
                .chatType(ChatType.TALK)
                .text(message)
                .build();


        return new ChatResponse(chatRepository.save(chat));
    }

    @Override
    @Transactional
    public Slice<ChatResponse> findChatsByRoom(Long chatRoomId, Pageable pageable, Member member) {

        Member currentMember = findMemberByIdOrThrowException(member.getMemberId());

        findChatRoomByIdOrThrowException(chatRoomId);

        if(!chatMemberRepository.existsByChatRoomIdAndMemberId(chatRoomId, currentMember.getMemberId())){
            throw new IllegalArgumentException(ChatExceptionMessage.CHATMEMBER_NOT_IN_CHATROOM.getText());
        }

        Slice<Chat> chats = chatRepository.findByChatRoomId(chatRoomId, pageable);

        List<Chat> unreadChats = chatRepository.findUnreadMessagesByChatRoomIdAndMemberId(chatRoomId, currentMember.getMemberId());

        if (unreadChats.isEmpty()) {
            return chats.map(ChatResponse::new);
        }

        unreadChats.forEach(Chat::markAsRead);

        String topic = "topic-chat-" + chatRoomId;
        MessageReadEvent readEvent = new MessageReadEvent(chatRoomId, currentMember.getMemberId(), null);
        chatProducer.sendReadEvent(topic, readEvent);

        return chats.map(ChatResponse::new);
    }

    @Override
    @Transactional
    public void updateMessageReadStatus(Long chatRoomId) {
        // TODO 나중에는 SpringSequrity에서 맴버 객체 받아서 사용할 예정
        Long memberId = 2L;

        // TODO 나중에 Member가 해당 채팅방에 속해있는지 검증 필요
        findChatRoomByIdOrThrowException(chatRoomId);

        List<Chat> unreadChats = chatRepository.findUnreadMessagesByChatRoomIdAndMemberId(chatRoomId, memberId);

        if (unreadChats.isEmpty()) {
            return;
        }

        unreadChats.forEach(Chat::markAsRead);

        MessageReadEvent readEvent = new MessageReadEvent(chatRoomId, memberId, null);

        String topicName = "topic-chat-" + chatRoomId;
        chatProducer.sendReadEvent(topicName, readEvent);
    }

    public void checkChat(ChatServiceRequest request){
        findChatRoomByIdOrThrowException(request.chatRoomId());
        findMemberByEmailOrThrowException(request.email());
    }

    private ChatRoom findChatRoomByIdOrThrowException(Long id) {
        return chatRoomRepository.findActiveById(id)
                .orElseThrow(() -> {
                    log.warn(">>>> {} : {} <<<<", id, ChatExceptionMessage.CHATROOM_NOT_FOUND);
                    return new IllegalArgumentException(ChatExceptionMessage.CHATROOM_NOT_FOUND.getText());
                });
    }

    private Member findMemberByEmailOrThrowException(String email) {
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.warn(">>>> {} : {} <<<<", email, ChatExceptionMessage.MEMBER_NOT_FOUND);
                    return new IllegalArgumentException(ChatExceptionMessage.MEMBER_NOT_FOUND.getText());
                });
    }

    private Member findMemberByIdOrThrowException(Long id) {
        return memberRepository.findActiveById(id)
                .orElseThrow(() -> {
                    log.warn(">>>> {} : {} <<<<", id, FamilyExceptionMessage.MEMBER_NOT_FOUND);
                    return new IllegalArgumentException(FamilyExceptionMessage.MEMBER_NOT_FOUND.getText());
                });
    }
}
