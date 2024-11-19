package team9.ddang.chat.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team9.ddang.chat.entity.ChatMember;
import team9.ddang.chat.entity.ChatRoom;
import team9.ddang.chat.repository.ChatMemberRepository;
import team9.ddang.chat.repository.ChatRoomRepository;
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

    @Transactional
    public ChatRoomResponse createChatRoom(Long opponentMemberId) {

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
            return new ChatRoomResponse(existingChatRoom.get());
        }
//        Optional<ChatRoom> existingChatRoom = chatRoomRepository.findOneToOneChatRoom(authenticatedMember, opponentMember);
//        if (existingChatRoom.isPresent()) {
//            return new ChatRoomResponse(existingChatRoom.get());
//        }

        // TODO 채팅방 이름은 어떻게 할까
        ChatRoom chatRoom = chatRoomRepository.save(ChatRoom.builder()
                .name("examplename" + " & " + opponentMember.getName())
                .build());


        chatMemberRepository.save(ChatMember.builder()
                .member(authenticatedMember)
                .chatRoom(chatRoom)
                .build());

        chatMemberRepository.save(ChatMember.builder()
                .member(opponentMember)
                .chatRoom(chatRoom)
                .build());


        kafkaDynamicListenerService.addListenerForChatRoom(chatRoom.getChatroomId());

        return new ChatRoomResponse(chatRoom);
    }

    @Transactional(readOnly = true)
    public List<ChatRoomResponse> getChatRoomsForAuthenticatedMember() {

        Member authenticatedMember = getAuthenticatedMember();

        List<ChatRoom> chatRooms = chatRoomRepository.findChatRoomsByMember(authenticatedMember);

        // 각 채팅방의 멤버 조회 후 ChatRoomResponse로 변환
        return chatRooms.stream()
                .map(chatRoom -> {
                    List<Member> members = chatMemberRepository.findMembersByChatRoom(chatRoom);
                    // TODO 나중에 채팅방 목록에 채팅방에 참여중인 인원에 대한 정보도 같이 반환하도록
                    return new ChatRoomResponse(chatRoom);
//                    return new ChatRoomResponse(chatRoom, members);
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
}
