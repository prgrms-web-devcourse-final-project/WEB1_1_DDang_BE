package team9.ddang.member.repository;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import team9.ddang.IntegrationTestSupport;
import team9.ddang.global.entity.Gender;
import team9.ddang.member.entity.*;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertAll;

@Transactional
class FriendRepositoryTest extends IntegrationTestSupport {

    @Autowired
    FriendRepository friendRepository;

    @Autowired
    MemberRepository memberRepository;

    @DisplayName("삭제 시 양쪽으로 걸려있는 친구를 모두 삭제한다.")
    @Test
    void deleteBySenderAndReceiver() {
        //given
        Member member = Member.builder()
                .name("test")
                .email("test@naver.com")
                .role(Role.USER)
                .isMatched(IsMatched.TRUE)
                .address("testAddress")
                .gender(Gender.MALE)
                .familyRole(FamilyRole.ELDER_BROTHER)
                .provider(Provider.NAVER)
                .profileImg("")
                .build();

        Member otherMember = Member.builder()
                .name("test2")
                .email("test2@naver.com")
                .role(Role.USER)
                .isMatched(IsMatched.TRUE)
                .address("test2Address")
                .gender(Gender.FEMALE)
                .familyRole(FamilyRole.ELDER_SISTER)
                .provider(Provider.NAVER)
                .profileImg("")
                .build();

        memberRepository.saveAll(Arrays.asList(member, otherMember));
        saveFriend(member, otherMember);

        //when
        friendRepository.deleteBySenderAndReceiver(member, otherMember);

        //then
        assertAll("친구 신청, 친구 테이블 확인",
                () -> Assertions.assertThat(friendRepository.existsBySenderAndReceiver(otherMember, otherMember)).isFalse(),
                () -> Assertions.assertThat(friendRepository.existsBySenderAndReceiver(member, otherMember)).isFalse()
        );
    }

    private void saveFriend(Member member, Member otherMemeber){
        Friend friend = Friend.builder()
                .sender(member)
                .receiver(otherMemeber)
                .build();

        Friend friend2 = Friend.builder()
                .sender(member)
                .receiver(otherMemeber)
                .build();

        friendRepository.saveAll(Arrays.asList(friend ,friend2));
    }

}