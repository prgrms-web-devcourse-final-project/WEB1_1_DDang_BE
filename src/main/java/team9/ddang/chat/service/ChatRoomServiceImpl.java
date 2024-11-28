package team9.ddang.chat.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team9.ddang.chat.entity.ChatMember;
import team9.ddang.chat.entity.ChatRoom;
import team9.ddang.chat.exception.ChatExceptionMessage;
import team9.ddang.chat.repository.ChatMemberRepository;
import team9.ddang.chat.repository.ChatRepository;
import team9.ddang.chat.repository.ChatRoomRepository;
import team9.ddang.chat.service.request.ChatRoomCreateServiceRequest;
import team9.ddang.chat.service.response.ChatRoomResponse;
import team9.ddang.member.entity.Member;
import team9.ddang.member.repository.MemberRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatRoomServiceImpl implements ChatRoomService {

    private final KafkaDynamicListenerService kafkaDynamicListenerService;
    private final ChatMemberRepository chatMemberRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final MemberRepository memberRepository;
    private final ChatRepository chatRepository;

    @Transactional
    public ChatRoomResponse createChatRoom(ChatRoomCreateServiceRequest request, Member member) {

        Member currentMember = findMemberByIdOrThrowException(member.getMemberId());

        Member opponentMember = findMemberByIdOrThrowException(request.opponentMemberId());

        List<Member> members = new ArrayList<>();
        members.add(currentMember);
        members.add(opponentMember);

        Optional<ChatRoom> existingChatRoom = chatRoomRepository.findOneToOneChatRoom(currentMember, opponentMember);
        if (existingChatRoom.isPresent()) {
            String lastMessage = getLastMessage(existingChatRoom.get().getChatroomId());
            Long unreadCount = chatRepository.countUnreadMessagesByChatRoomAndMember(existingChatRoom.get().getChatroomId(), currentMember.getMemberId());
            return new ChatRoomResponse(existingChatRoom.get(), lastMessage, unreadCount, members);
        }

        ChatRoom chatRoom = chatRoomRepository.save(ChatRoom.builder()
                .name(currentMember.getName() + " & " + opponentMember.getName())
                .build());

        chatMemberRepository.save(ChatMember.builder()
                .member(currentMember)
                .chatRoom(chatRoom)
                .build());

        chatMemberRepository.save(ChatMember.builder()
                .member(opponentMember)
                .chatRoom(chatRoom)
                .build());


        kafkaDynamicListenerService.addListenerForChatRoom(chatRoom.getChatroomId());

        return new ChatRoomResponse(chatRoom, null, 0L, members);
    }

    @Transactional(readOnly = true)
    public List<ChatRoomResponse> getChatRoomsForAuthenticatedMember(Member member) {

        Member currentMember = findMemberByIdOrThrowException(member.getMemberId());

        List<ChatRoom> chatRooms = chatRoomRepository.findChatRoomsByMember(currentMember);

        return chatRooms.stream()
                .map(chatRoom -> {
                    String lastMessage = chatRepository.findLastMessageByChatRoom(chatRoom.getChatroomId());
                    List<Member> members = chatMemberRepository.findMembersByChatRoom(chatRoom);
                    Long unreadCount = chatRepository.countUnreadMessagesByChatRoomAndMember(chatRoom.getChatroomId(), currentMember.getMemberId());
                    return new ChatRoomResponse(chatRoom, lastMessage, unreadCount, members);
                })
                .toList();
    }

    private Member findMemberByIdOrThrowException(Long id) {
        return memberRepository.findActiveById(id)
                .orElseThrow(() -> {
                    log.warn(">>>> {} : {} <<<<", id, ChatExceptionMessage.MEMBER_NOT_FOUND);
                    return new IllegalArgumentException(ChatExceptionMessage.MEMBER_NOT_FOUND.getText());
                });
    }

    private String getLastMessage(Long chatRoomId) {
        return chatRepository.findLastMessageByChatRoom(chatRoomId);
    }
}
