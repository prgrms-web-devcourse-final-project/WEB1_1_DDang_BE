package team9.ddang.chat.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
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
import team9.ddang.chat.service.request.ChatReadServiceRequest;
import team9.ddang.chat.service.request.ChatServiceRequest;
import team9.ddang.chat.service.response.ChatReadResponse;
import team9.ddang.chat.service.response.ChatResponse;
import team9.ddang.family.exception.FamilyExceptionMessage;
import team9.ddang.member.entity.Member;
import team9.ddang.member.repository.MemberRepository;

import java.time.LocalDateTime;
import java.util.Collections;
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

        validateMemberInChatRoom(chatRoomId, member);

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

        Member currentMember = checkValidate(chatRoomId, member.getEmail());

        Slice<Chat> chats = chatRepository.findByChatRoomId(chatRoomId, pageable);

        chatProducer.sendReadEvent(chatRoomId, new ChatReadServiceRequest(chatRoomId, currentMember.getEmail(), null));

        List<ChatResponse> reversedResponses = new java.util.ArrayList<>(chats.getContent()
                .stream()
                .map(ChatResponse::new)
                .toList());
        Collections.reverse(reversedResponses);

        return new SliceImpl<>(reversedResponses, pageable, chats.hasNext());
    }

    @Override
    @Transactional
    public Slice<ChatResponse> findChatsBefore(Long chatRoomId, LocalDateTime lastMessageCreatedAt, Pageable pageable, Member member){
        checkValidate(chatRoomId, member.getEmail());
        Slice<Chat> chats = chatRepository.findChatsBefore(chatRoomId, lastMessageCreatedAt, pageable);
        List<ChatResponse> reversedResponses = new java.util.ArrayList<>(chats.getContent()
                .stream()
                .map(ChatResponse::new)
                .toList());
        Collections.reverse(reversedResponses);

        return new SliceImpl<>(reversedResponses, pageable, chats.hasNext());
    }

    @Override
    @Transactional
    public ChatReadResponse updateMessageReadStatus(Long chatRoomId, String email) {

        Member member = checkValidate(chatRoomId, email);

        List<Chat> unreadChats = chatRepository.findUnreadMessagesByChatRoomIdAndMemberId(chatRoomId, member.getMemberId());

        unreadChats.forEach(Chat::markAsRead);

        return new ChatReadResponse(chatRoomId, email, null);
    }

    public void checkChat(ChatServiceRequest request){
        findChatRoomByIdOrThrowException(request.chatRoomId());
        findMemberByEmailOrThrowException(request.email());
    }

    private void validateMemberInChatRoom(Long chatRoomId, Member member) {
        boolean isMemberInChatRoom = chatMemberRepository.existsByChatRoomIdAndMemberId(chatRoomId, member.getMemberId());
        if (!isMemberInChatRoom) {
            throw new IllegalArgumentException(ChatExceptionMessage.CHATMEMBER_NOT_IN_CHATROOM.getText());
        }
    }

    private Member checkValidate(Long chatRoomId, String email){
        findChatRoomByIdOrThrowException(chatRoomId);

        Member member = findMemberByEmailOrThrowException(email);

        if(!chatMemberRepository.existsByChatRoomIdAndMemberId(chatRoomId, member.getMemberId())){
            throw new IllegalArgumentException(ChatExceptionMessage.CHATMEMBER_NOT_IN_CHATROOM.getText());
        }

        return member;
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
