package team9.ddang.member.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import team9.ddang.IntegrationTestSupport;
import team9.ddang.global.entity.Gender;
import team9.ddang.member.controller.request.AddFriendRequest;
import team9.ddang.member.entity.*;
import team9.ddang.member.repository.FriendRepository;
import team9.ddang.member.repository.FriendRequestRepository;
import team9.ddang.member.repository.MemberRepository;
import team9.ddang.member.service.response.FriendListResponse;
import team9.ddang.member.service.response.FriendResponse;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.tuple;
import static org.junit.jupiter.api.Assertions.assertAll;

@Transactional
class FriendServiceImplTest extends IntegrationTestSupport {

    @Autowired
    private FriendService friendService;

    @Autowired
    private FriendRepository friendRepository;

    @Autowired
    private FriendRequestRepository friendRequestRepository;

    @Autowired
    private MemberRepository memberRepository;

    @BeforeEach
    void createMember(){
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
    }


    @DisplayName("친구 요청이 없으면 친구 요청을 생성한다.")
    @Test
    void acceptFriendWithoutFriendRequest() {

        //given
        Member member = memberRepository.findByEmail("test@naver.com").get();
        Member otherMember = memberRepository.findByEmail("test2@naver.com").get();
        AddFriendRequest request = new AddFriendRequest(otherMember.getMemberId(), "ACCEPT");

        //when
        friendService.decideFriend(member, request);

        //then
        assertThat(friendRequestRepository.existsFriendRequestBySenderAndReceiver(member,otherMember))
                .isTrue();
    }


    @DisplayName("상대가 보낸 친구 요청이 존재하면 친구 요청을 삭제 하고 친구를 생성한다.")
    @Test
    void acceptFriendWithFriendRequest() {
        //given
        Member member = memberRepository.findByEmail("test@naver.com").get();
        Member otherMember = memberRepository.findByEmail("test2@naver.com").get();
        AddFriendRequest request = new AddFriendRequest(otherMember.getMemberId(), "ACCEPT");
        FriendRequest friendRequest = FriendRequest.builder()
                .sender(otherMember)
                .receiver(member)
                .build();

        //when
        friendRequestRepository.save(friendRequest);
        friendService.decideFriend(member, request);

        //then
        assertAll("친구 신청, 친구 테이블 확인",
                () -> assertThat(friendRequestRepository.existsFriendRequestBySenderAndReceiver(member, otherMember)).isFalse(),
                () -> assertThat(friendRepository.existsBySenderAndReceiver(member, otherMember)).isTrue()
        );

    }

    @DisplayName("친구 요청이 존재하면 친구 요청을 삭제 한다.")
    @Test
    void denyFriendWithFriendRequest() {
        //given
        Member member = memberRepository.findByEmail("test@naver.com").get();
        Member otherMember = memberRepository.findByEmail("test2@naver.com").get();
        AddFriendRequest request = new AddFriendRequest(otherMember.getMemberId(), "DENY");
        FriendRequest friendRequest = FriendRequest.builder()
                .sender(otherMember)
                .receiver(member)
                .build();


        //when
        friendRequestRepository.save(friendRequest);
        friendService.decideFriend(member, request);

        //then
        assertThat(friendRequestRepository.existsFriendRequestBySenderAndReceiver(member,otherMember))
                .isFalse();
    }


    @Test
    void getFriendList() {
        //given
        Member member = memberRepository.findByEmail("test@naver.com").get();
        Member otherMember = memberRepository.findByEmail("test2@naver.com").get();
        saveFriend(member, otherMember);

        //when
        List<FriendListResponse> friendList = friendService.getFriendList(member);

        //then
        assertThat(friendList)
                .hasSize(1)
                .extracting("memberId", "gender", "familyRole", "profileImg", "name")
                .containsExactlyInAnyOrder(
                        tuple(otherMember.getMemberId(), otherMember.getGender(), otherMember.getFamilyRole(), otherMember.getProfileImg(), otherMember.getName())
                );

    }

    @DisplayName("친구의 상세프로필을 조회한다.")
    @Test
    void getFriendProfile() {
        //given
        Member member = memberRepository.findByEmail("test@naver.com").get();
        Member otherMember = memberRepository.findByEmail("test2@naver.com").get();
        saveFriend(member, otherMember);
        
        //when
        FriendResponse response = friendService.getFriend(member, otherMember.getMemberId());

        //then
        assertThat(response).isNotNull()
                .extracting("memberName", "address", "memberGender", "familyRole", "memberProfileImg")
                .containsExactlyInAnyOrder(
                  otherMember.getName(), otherMember.getAddress(), otherMember.getGender(), otherMember.getFamilyRole(), otherMember.getProfileImg()
                );
    }

    @DisplayName("친구가 아닌 사람의 상세프로필을 조회할 시 에러를 던진다.")
    @Test
    void getFriendProfileWhenIsNotFriendThrowException() {
        //given
        Member member = memberRepository.findByEmail("test@naver.com").get();
        Member otherMember = memberRepository.findByEmail("test2@naver.com").get();

        //when
        //then
        assertThatThrownBy(() -> friendService.getFriend(member, otherMember.getMemberId()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("친구가 아닌 사람의 프로필은 볼 수 없습니다.");
    }

    @Test
    @DisplayName("친구를 삭제한다.")
    void deleteFriend() {
        //given
        Member member = memberRepository.findByEmail("test@naver.com").get();
        Member otherMember = memberRepository.findByEmail("test2@naver.com").get();
        saveFriend(member, otherMember);

        //when
        friendService.deleteFriend(member, otherMember.getMemberId());

        //then
        assertThat(friendRepository.existsBySenderAndReceiver(member, otherMember))
                .isFalse();

    }

    @Test
    @DisplayName("친구아닌 사람을 삭제할 시 에러를 던진다.")
    void deleteFriendWhenIsNotFriendThrowException() {
        //given
        Member member = memberRepository.findByEmail("test@naver.com").get();
        Member otherMember = memberRepository.findByEmail("test2@naver.com").get();

        //when
        assertThatThrownBy(() -> friendService.deleteFriend(member, otherMember.getMemberId()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("친구가 아닌 사람을 삭제할 수 없습니다.");
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