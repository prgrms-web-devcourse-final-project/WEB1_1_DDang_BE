package team9.ddang.member.service;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team9.ddang.dog.entity.Dog;
import team9.ddang.dog.repository.MemberDogRepository;
import team9.ddang.global.api.WebSocketResponse;
import team9.ddang.member.controller.request.AddFriendRequest;
import team9.ddang.member.entity.Friend;
import team9.ddang.member.entity.FriendRequest;
import team9.ddang.member.entity.Member;
import team9.ddang.member.repository.FriendRepository;
import team9.ddang.member.repository.FriendRequestRepository;
import team9.ddang.member.repository.MemberRepository;
import team9.ddang.member.repository.WalkWithMemberRepository;
import team9.ddang.member.service.response.FriendListResponse;
import team9.ddang.member.service.response.FriendResponse;
import team9.ddang.member.service.response.MemberResponse;
import team9.ddang.notification.entity.Notification;
import team9.ddang.notification.repository.NotificationRepository;
import team9.ddang.notification.service.request.FriendNotificationRequest;
import team9.ddang.walk.repository.WalkRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FriendServiceImpl implements FriendService{

    private final MemberRepository memberRepository;
    private final FriendRequestRepository friendRequestRepository;
    private final FriendRepository friendRepository;
    private final MemberDogRepository memberDogRepository;
    private final WalkRepository walkRepository;
    private final WalkWithMemberRepository walkWithMemberRepository;
    private final NotificationRepository notificationRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @Override
    @Transactional
    public MemberResponse addFriend(Member member, AddFriendRequest addFriendRequest) {
        Member otherMember = getMemberFromMemberIdOrElseThrow(addFriendRequest.memberId());

        if(friendRequestRepository.existsFriendRequestByReceiver(member)){
            saveFriend(member, otherMember);

            FriendNotificationRequest request1 = new FriendNotificationRequest(otherMember, member);
            FriendNotificationRequest request2 = new FriendNotificationRequest(member, otherMember);

            Notification notificationForMember = request1.toEntity();
            Notification notificationForOtherMember = request2.toEntity();

            notificationRepository.saveAll(List.of(notificationForMember, notificationForOtherMember));

            sendMessageToNotificationUrl(member.getEmail(), notificationForMember.getContent());
            sendMessageToNotificationUrl(otherMember.getEmail(), notificationForOtherMember.getContent());
        }
        else {
            createFriendRequest(member, otherMember);
        }

        return MemberResponse.from(otherMember);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FriendListResponse> getFriendList(Member member) {
        List<Member> friends = friendRepository.findAllFriendsBySender(member);

        return friends.stream().map(FriendListResponse::from).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public FriendResponse getFriend(Member member, Long memberId) {
        Member otherMember = getMemberFromMemberIdOrElseThrow(memberId);

        if(!friendRepository.existsBySenderAndReceiver(member, otherMember)){
            throw new IllegalArgumentException("친구가 아닌 사람의 프로필은 볼 수 없습니다.");
        }

        Dog dog = memberDogRepository.findMemberDogByMemberId(memberId)
                .orElseThrow(() -> new IllegalArgumentException("강아지가 존재하지 않습니다.")).getDog();

        int totalDistanceInMeters = walkRepository.findTotalDistanceByMemberId(memberId);
        int countWalks = walkRepository.countWalksByMemberId(memberId);
        double totalDistanceInKilometers = totalDistanceInMeters / 1000.0;
        int countWalksWithMember = walkWithMemberRepository.countBySenderMemberId(memberId);

        return FriendResponse.of(otherMember, dog, totalDistanceInKilometers, countWalks ,countWalksWithMember);
    }

    @Override
    @Transactional
    public void deleteFriend(Member member, Long memberId) {
        Member otherMember = getMemberFromMemberIdOrElseThrow(memberId);

        if(!friendRepository.existsBySenderAndReceiver(member, otherMember)){
            throw new IllegalArgumentException("친구가 아닌 사람을 삭제할 수 없습니다.");
        }

        friendRepository.deleteBySenderAndReceiver(member, otherMember);
    }

    private void saveFriend(Member member, Member otherMember){
        List<Friend> friends = List.of(
                Friend.builder().sender(member).receiver(otherMember).build(),
                Friend.builder().sender(otherMember).receiver(member).build()
        );

        friendRepository.saveAll(friends);
        friendRequestRepository.deleteByReceiver(member);
    }

    private void createFriendRequest(Member member, Member otherMember){
        // 친구 요청 추가
        FriendRequest friendRequest = FriendRequest.builder()
                .sender(member)
                .receiver(otherMember)
                .build();

        friendRequestRepository.save(friendRequest);
    }

    private Member getMemberFromMemberIdOrElseThrow(Long memberId){
        return memberRepository.findById(memberId).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 멤버 입니다."));
    }

    private void sendMessageToNotificationUrl(String email, Object data){
        messagingTemplate.convertAndSend("/sub/notification/" + email,  WebSocketResponse.ok(data));
    }
}
