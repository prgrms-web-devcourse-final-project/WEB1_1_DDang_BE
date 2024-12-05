package team9.ddang.chat.repository;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;
import team9.ddang.IntegrationTestSupport;
import team9.ddang.chat.entity.Chat;
import team9.ddang.chat.entity.ChatRoom;
import team9.ddang.chat.entity.ChatType;
import team9.ddang.global.entity.Gender;
import team9.ddang.member.entity.*;
import team9.ddang.member.repository.MemberRepository;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@Transactional
class ChatRepositoryTest extends IntegrationTestSupport {

    @Autowired
    private ChatRepository chatRepository;

    @Autowired
    private ChatRoomRepository chatRoomRepository;

    @Autowired
    private MemberRepository memberRepository;

    @DisplayName("채팅방 ID로 채팅 목록을 조회한다.")
    @Test
    void findByChatRoomId_shouldReturnChats() {

        ChatRoom chatRoom = chatRoomRepository.save(ChatRoom.builder()
                .name("Test Chat Room")
                .build());

        Member member = memberRepository.save(Member.builder()
                .name("Test Member")
                .email("test@example.com")
                .gender(Gender.MALE)
                .address("123 Test Street")
                .provider(Provider.GOOGLE)
                .role(Role.USER)
                .isMatched(IsMatched.TRUE)
                .familyRole(FamilyRole.FATHER)
                .profileImg("test profile img")
                .build());

        Chat chat1 = chatRepository.save(Chat.builder()
                .chatRoom(chatRoom)
                .member(member)
                .text("Hello World 1")
                .chatType(ChatType.TALK)
                .build());

        Chat chat2 = chatRepository.save(Chat.builder()
                .chatRoom(chatRoom)
                .member(member)
                .text("Hello World 2")
                .chatType(ChatType.TALK)
                .build());

        List<Chat> chats = chatRepository.findByChatRoomId(chatRoom.getChatroomId(), PageRequest.of(0, 10))
                .getContent();

        assertThat(chats).hasSize(2)
                .extracting("text")
                .containsExactlyInAnyOrder("Hello World 1", "Hello World 2");
    }

    @DisplayName("채팅방의 마지막 메시지를 조회한다.")
    @Test
    void findLastMessageByChatRoom_shouldReturnLastMessage() {
        ChatRoom chatRoom = chatRoomRepository.save(ChatRoom.builder()
                .name("Test Chat Room")
                .build());

        Member member = memberRepository.save(Member.builder()
                .name("Test Member")
                .email("test@example.com")
                .gender(Gender.MALE)
                .provider(Provider.GOOGLE)
                .role(Role.USER)
                .address("123 Test Street")
                .isMatched(IsMatched.TRUE)
                .familyRole(FamilyRole.FATHER)
                .profileImg("test profile img")
                .build());

        Chat lastChat = chatRepository.save(Chat.builder()
                .chatRoom(chatRoom)
                .member(member)
                .text("Last Message")
                .chatType(ChatType.TALK)
                .build());

        String lastMessage = chatRepository.findLastMessageByChatRoom(chatRoom.getChatroomId());

        assertThat(lastMessage).isNotNull();
        assertThat(lastMessage).isEqualTo(lastChat.getText());
    }
}