package team9.ddang.chat.service;

import jakarta.persistence.LockTimeoutException;
import jakarta.persistence.PessimisticLockException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
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
import team9.ddang.global.api.WebSocketResponse;
import team9.ddang.member.entity.Member;
import team9.ddang.member.repository.MemberRepository;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatRoomServiceImpl implements ChatRoomService {

    private final KafkaDynamicListenerService kafkaDynamicListenerService;
    private final ChatMemberRepository chatMemberRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final MemberRepository memberRepository;
    private final ChatRepository chatRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @Transactional
    public ChatRoomResponse createChatRoom(ChatRoomCreateServiceRequest request, Member member) {
        try {
            Member currentMember = findMemberByIdOrThrowException(member.getMemberId());
            Member opponentMember = findMemberByIdOrThrowException(request.opponentMemberId());

            List<Member> members = List.of(opponentMember);
            List<Member> curmembers = List.of(currentMember);

            return chatRoomRepository.findOneToOneChatRoom(currentMember, opponentMember)
                    .map(existingChatRoom -> {
                        String lastMessage = getLastMessage(existingChatRoom.getChatroomId());
                        Long unreadCount = chatRepository.countUnreadMessagesByChatRoomAndMember(
                                existingChatRoom.getChatroomId(),
                                currentMember.getMemberId()
                        );
                        return new ChatRoomResponse(existingChatRoom, lastMessage, unreadCount, members);
                    })
                    .orElseGet(() -> {
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

                        sendMessageToUser(opponentMember.getEmail(),
                                new ChatRoomResponse(chatRoom, null, 0L, curmembers));

                        return new ChatRoomResponse(chatRoom, null, 0L, members);
                    });
        } catch (PessimisticLockException | LockTimeoutException e) {
            throw new IllegalArgumentException(ChatExceptionMessage.CHATROOM_CREATION_FAILED.getText());
        }
    }

    @Transactional(readOnly = true)
    public List<ChatRoomResponse> getChatRoomsForAuthenticatedMember(Member member) {

        Member currentMember = findMemberByIdOrThrowException(member.getMemberId());

        List<ChatRoom> chatRooms = chatRoomRepository.findChatRoomsByMember(currentMember);

        return chatRooms.stream()
                .map(chatRoom -> {
                    String lastMessage = chatRepository.findLastMessageByChatRoom(chatRoom.getChatroomId());
                    List<Member> allMembers = chatMemberRepository.findMembersByChatRoom(chatRoom);
                    List<Member> otherMembers = allMembers.stream()
                            .filter(m -> !m.getMemberId().equals(currentMember.getMemberId()))
                            .toList();
                    Long unreadCount = chatRepository.countUnreadMessagesByChatRoomAndMember(chatRoom.getChatroomId(), currentMember.getMemberId());
                    return new ChatRoomResponse(chatRoom, lastMessage, unreadCount, otherMembers);
                })
                .toList();
    }

    private void sendMessageToUser(String email, Object data) {
        messagingTemplate.convertAndSend("/sub/chatroom/" + email, WebSocketResponse.created(data));
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
