package team9.ddang.walk.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.geo.GeoResult;
import org.springframework.data.geo.GeoResults;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team9.ddang.dog.entity.Dog;
import team9.ddang.dog.repository.MemberDogRepository;
import team9.ddang.global.api.WebSocketResponse;
import team9.ddang.global.service.RedisService;
import team9.ddang.member.entity.IsMatched;
import team9.ddang.member.entity.Member;
import team9.ddang.member.repository.MemberRepository;
import team9.ddang.walk.service.request.walk.DecisionWalkServiceRequest;
import team9.ddang.walk.service.request.walk.ProposalWalkServiceRequest;
import team9.ddang.walk.service.request.walk.StartWalkServiceRequest;
import team9.ddang.walk.service.response.walk.*;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import static team9.ddang.walk.exception.WalkExceptionMessage.*;
import static team9.ddang.walk.service.RedisKey.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class WalkLocationServiceImpl implements WalkLocationService {

    private final RedisService redisService;
    private final MemberDogRepository memberDogRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final MemberRepository memberRepository;


    @Override
    @Transactional(readOnly = true)
    public void startWalk(String email, StartWalkServiceRequest startWalkServiceRequest){
        saveMemberLocation(email, startWalkServiceRequest);
        findNearbyMember(email);
    }

    @Override
    public void proposalWalk(String email, ProposalWalkServiceRequest proposalWalkServiceRequest) {
        Member member = getMemberFromEmailOrElseThrow(email);

        Dog dog = getDogFromMemberId(member.getMemberId());
        String otherEmail = proposalWalkServiceRequest.otherMemberEmail();
        validateBeforeProposalWalk(email, otherEmail);

        redisService.setValues(PROPOSAL_KEY + member.getEmail(), otherEmail, Duration.ofMinutes(3));
        ProposalWalkResponse response = ProposalWalkResponse.of(dog, member, proposalWalkServiceRequest.comment());

        sendMessageToWalkUrl(otherEmail, response);
        sendMessageToWalkUrl(member.getEmail(), response);
    }

    @Override
    public void decisionWalk(String email, DecisionWalkServiceRequest serviceRequest) {
        Member member = getMemberFromEmailOrElseThrow(email);
        String memberEmail = redisService.getValues(PROPOSAL_KEY + serviceRequest.otherEmail());

        if(memberEmail == null){
            throw new IllegalArgumentException(NOT_EXIST_PROPOSAL.getText());
        }

        if(!memberEmail.equals(member.getEmail())){
            throw new IllegalArgumentException(NOT_MATCHED_MEMBER.getText());
        }

        Member otherMember = getMemberFromEmailOrElseThrow(memberEmail);
        redisService.deleteValues(PROPOSAL_KEY + serviceRequest.otherEmail());

        if(serviceRequest.decision().equals("ACCEPT")){
            redisService.setValues(WALK_WITH_KEY + member.getEmail(), serviceRequest.otherEmail());
            redisService.setValues(WALK_WITH_KEY + serviceRequest.otherEmail(), member.getEmail());
        }

        sendMessageToWalkUrl(member.getEmail(), DecisionWalkResponse.of(serviceRequest.decision(), otherMember));
        sendMessageToWalkUrl(serviceRequest.otherEmail(), DecisionWalkResponse.of(serviceRequest.decision(), member));
    }

    @Override
    public void startWalkWith(String email, StartWalkServiceRequest startWalkServiceRequest) {
        saveMemberLocation(email, startWalkServiceRequest);
        String otherMemberEmail = redisService.getValues(WALK_WITH_KEY + email);

        if(otherMemberEmail == null){
            throw new IllegalArgumentException(EMAIL_NOT_FOUND.getText());
        }

        sendMessageToWalkUrl(otherMemberEmail, WalkWithResponse.of(email, startWalkServiceRequest));
    }

    private void saveMemberLocation(String email, StartWalkServiceRequest startWalkServiceRequest){
        // 좌표 데이터를 String 변환
        String pointData = startWalkServiceRequest.toStringFormat();
        Point point = new Point(startWalkServiceRequest.longitude(), startWalkServiceRequest.latitude());

        redisService.setGeoValues(POINT_KEY, email, point);
        redisService.setListValues(LIST_KEY + email, pointData);
    }

    private void findNearbyMember(String email){
        Point memberLocation = redisService.getMemberPoint(POINT_KEY, email);
        GeoResults<RedisGeoCommands.GeoLocation<String>> results = redisService.getNearbyMemberResults(memberLocation, 200, 10);
        List<String> memberEmailList = getEmailListFromNearbyMemberResults(results, email);

        if(memberEmailList.isEmpty()){
            sendMessageToWalkUrl(email, null);
            return;
        }

        List<MemberNearbyInfo> memberNearbyInfos = memberDogRepository.findDogsAndMembersByMemberEmails(memberEmailList);
        sendNearbyMember(memberNearbyInfos, email);
    }

    private void sendNearbyMember(List<MemberNearbyInfo> memberNearbyInfos, String email){
        List<MemberNearbyResponse> responseList = memberNearbyInfos.stream()
                .filter(memberNearbyInfo -> memberNearbyInfo.isMatched().equals(IsMatched.TRUE))
                .map(MemberNearbyResponse::from).toList();

        sendMessageToWalkUrl(email, responseList);
    }

    private List<String> getEmailListFromNearbyMemberResults(GeoResults<RedisGeoCommands.GeoLocation<String>> results, String email){
        List<String> memberEmailList = new ArrayList<>();

        for(GeoResult<RedisGeoCommands.GeoLocation<String>> result : results) {
            RedisGeoCommands.GeoLocation<String> location = result.getContent();
            String memberNearbyEmail = location.getName();

            if(!memberNearbyEmail.equals(email) && !redisService.checkHasKey(WALK_WITH_KEY + memberNearbyEmail)){
                memberEmailList.add(memberNearbyEmail);
            }
        }

        return memberEmailList;
    }

    private void sendMessageToWalkUrl(String email, Object data){
        messagingTemplate.convertAndSend("/sub/walk/" + email,  WebSocketResponse.ok(data));
        log.info("Message sent to /sub/walk/" + email + " with data: " + data);
    }

    private Member getMemberFromEmailOrElseThrow(String email){
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException(MEMBER_NOT_FOUND.getText()));
    }

    private Dog getDogFromMemberId(Long memberId){
        return memberDogRepository.findMemberDogByMemberId(memberId)
                .orElseThrow(() -> new IllegalArgumentException(DOG_NOT_FOUND.getText())).getDog();
    }

    private void validateBeforeProposalWalk(String email, String otherEmail){
        if(redisService.checkHasKey(PROPOSAL_KEY + email)){
            throw new IllegalArgumentException(ALREADY_PROPOSAL.getText());
        }

        if(redisService.checkHasKey(WALK_WITH_KEY + email) || redisService.checkHasKey(WALK_WITH_KEY + otherEmail)){
            throw new IllegalArgumentException(ALREADY_MATCHED_MEMBER.getText());
        }

    }
}
