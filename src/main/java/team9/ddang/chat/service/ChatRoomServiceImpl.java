package team9.ddang.chat.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team9.ddang.chat.entity.ChatMember;
import team9.ddang.chat.entity.ChatRoom;
import team9.ddang.chat.repository.ChatMemberRepository;
import team9.ddang.chat.repository.ChatRepository;
import team9.ddang.chat.repository.ChatRoomRepository;
import team9.ddang.chat.service.request.ChatRoomCreateServiceRequest;
import team9.ddang.chat.service.response.ChatRoomResponse;
import team9.ddang.member.entity.Member;
import team9.ddang.member.repository.MemberRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChatRoomServiceImpl implements ChatRoomService{

    private final KafkaDynamicListenerService kafkaDynamicListenerService;
    private final ChatMemberRepository chatMemberRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final MemberRepository memberRepository;
    private final ChatRepository chatRepository;

    @Transactional
    public ChatRoomResponse createChatRoom(ChatRoomCreateServiceRequest request) {

        Long opponentMemberId = request.opponentMemberId();

        Member authenticatedMember = getAuthenticatedMember();

        // TODO 시큐리티 들어오면 지울 코드
        Member opponentMember1 = memberRepository.findById(2L)
                .orElseThrow(() -> new EntityNotFoundException("상대방 회원이 존재하지 않습니다."));


        // TODO 맴버 유효성 검사
        Member opponentMember = memberRepository.findById(opponentMemberId)
                .orElseThrow(() -> new EntityNotFoundException("상대방 회원이 존재하지 않습니다."));


        // TODO 시큐리티 들어오면 교체할 코드
        Optional<ChatRoom> existingChatRoom = chatRoomRepository.findOneToOneChatRoom(opponentMember1, opponentMember);
        if (existingChatRoom.isPresent()) {
            String lastMessage = getLastMessage(existingChatRoom.get().getChatroomId());
            Long unreadCount = chatRepository.countUnreadMessagesByChatRoomAndMember(existingChatRoom.get().getChatroomId(), authenticatedMember.getMemberId());
            return new ChatRoomResponse(existingChatRoom.get(), lastMessage, unreadCount);

        }
//        Optional<ChatRoom> existingChatRoom = chatRoomRepository.findOneToOneChatRoom(authenticatedMember, opponentMember);
//        if (existingChatRoom.isPresent()) {
//            String lastMessage = chatRepository.findLastMessageByChatRoom(existingChatRoom.get().getChatroomId());
//            return new ChatRoomResponse(existingChatRoom.get(), lastMessage, unreadCount, members);
//        }

        // TODO 채팅방 이름은 어떻게 할까
        ChatRoom chatRoom = chatRoomRepository.save(ChatRoom.builder()
                .name("examplename" + " & " + opponentMember.getName())
                .build());


        // TODO opponentMember1 -> authenticatedMember로 수정할것
        chatMemberRepository.save(ChatMember.builder()
                .member(opponentMember1)
                .chatRoom(chatRoom)
                .build());

        chatMemberRepository.save(ChatMember.builder()
                .member(opponentMember)
                .chatRoom(chatRoom)
                .build());


        kafkaDynamicListenerService.addListenerForChatRoom(chatRoom.getChatroomId());

        return new ChatRoomResponse(chatRoom, null,0L);
    }

    @Transactional(readOnly = true)
    public List<ChatRoomResponse> getChatRoomsForAuthenticatedMember() {

        Member authenticatedMember = getAuthenticatedMember();

        List<ChatRoom> chatRooms = chatRoomRepository.findChatRoomsByMember(authenticatedMember);

        return chatRooms.stream()
                .map(chatRoom -> {
                    String lastMessage = chatRepository.findLastMessageByChatRoom(chatRoom.getChatroomId());
                    List<Member> members = chatMemberRepository.findMembersByChatRoom(chatRoom);
                    Long unreadCount = chatRepository.countUnreadMessagesByChatRoomAndMember(chatRoom.getChatroomId(), authenticatedMember.getMemberId());
                    // TODO 나중에 채팅방 목록에 채팅방에 참여중인 인원에 대한 정보도 같이 반환하도록
                    return new ChatRoomResponse(chatRoom, lastMessage, unreadCount);
//                    return new ChatRoomResponse(chatRoom, lastMessage, unreadCount, members);
                })
                .toList();
    }

    private Member getAuthenticatedMember() {
        // TODO: 시큐리티 들어오면 교체할 코드
        return null;
    }
//    private Member getAuthenticatedMember(@AuthenticationPrincipal MemberDetails memberDetails) {
//        return memberDetails.getMember();
//    }
    private String getLastMessage(Long chatRoomId) {
        return chatRepository.findLastMessageByChatRoom(chatRoomId);
    }
}
