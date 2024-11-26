package team9.ddang.walk.service;

import lombok.RequiredArgsConstructor;
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
import team9.ddang.walk.service.request.DecisionWalkServiceRequest;
import team9.ddang.walk.service.request.ProposalWalkServiceRequest;
import team9.ddang.walk.service.request.StartWalkServiceRequest;
import team9.ddang.walk.service.response.MemberNearbyInfo;
import team9.ddang.walk.service.response.MemberNearbyResponse;
import team9.ddang.walk.service.response.ProposalWalkResponse;
import team9.ddang.walk.service.response.WalkWithResponse;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import static team9.ddang.walk.service.RedisKey.*;

@Service
@RequiredArgsConstructor
public class WalkLocationServiceImpl implements WalkLocationService {

    private final RedisService redisService;
    private final MemberDogRepository memberDogRepository;
    private final SimpMessagingTemplate messagingTemplate;


    @Override
    @Transactional(readOnly = true)
    public void startWalk(String email, StartWalkServiceRequest startWalkServiceRequest){
        saveMemberLocation(email, startWalkServiceRequest);
        findNearbyMember(email);
    }

    @Override
    public void proposalWalk(Member member, ProposalWalkServiceRequest proposalWalkServiceRequest) {
        Dog dog = memberDogRepository.findMemberDogByMemberId(member.getMemberId())
                .orElseThrow(() -> new IllegalArgumentException("개가 존재하지 않습니다.")).getDog();
        String otherEmail = proposalWalkServiceRequest.otherMemberEmail();

        if(redisService.checkHasKey(PROPOSAL_KEY + member.getEmail())){
            throw new IllegalArgumentException("이미 다른 견주분에게 산책을 제안을 하신 상태 입니다.");
        }

        redisService.setValues(PROPOSAL_KEY + member.getEmail(), otherEmail, Duration.ofMinutes(3));
        ProposalWalkResponse response = ProposalWalkResponse.of(dog, member, proposalWalkServiceRequest.comment());

        sendMessageToWalkUrl(otherEmail, response);
        sendMessageToWalkUrl(member.getEmail(), response);
    }

    @Override
    public void decisionWalk(Member member, DecisionWalkServiceRequest serviceRequest) {
        String memberEmail = redisService.getValues(PROPOSAL_KEY + serviceRequest.otherEmail());

        if(memberEmail == null){
            throw new IllegalArgumentException("제안을 취소했거나 이미 강번따를 진행 중인 유저 입니다.");
        }

        if(!memberEmail.equals(member.getEmail())){
            throw new IllegalArgumentException("제안을 한 유저와 받은 유저가 일치하지 않습니다.");
        }

        redisService.deleteValues(PROPOSAL_KEY + serviceRequest.otherEmail());

        redisService.setValues(WALK_WITH_KEY + member.getEmail(), serviceRequest.otherEmail());
        redisService.setValues(WALK_WITH_KEY + serviceRequest.otherEmail(), member.getEmail());

        sendMessagetoWalkRequestUrl(member.getEmail(), serviceRequest.decision());
        sendMessagetoWalkRequestUrl(serviceRequest.otherEmail(), serviceRequest.decision());
    }

    @Override
    public void startWalkWith(String email, StartWalkServiceRequest startWalkServiceRequest) {
        saveMemberLocation(email, startWalkServiceRequest);
        String otherMemberEmail = redisService.getValues(WALK_WITH_KEY + email);

        if(otherMemberEmail == null){
            throw new IllegalArgumentException("상대 이메일 정보가 존재하지 않습니다.");
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

            if(!memberNearbyEmail.equals(email)){
                memberEmailList.add(memberNearbyEmail);
            }
        }

        return memberEmailList;
    }

    private void sendMessageToWalkUrl(String email, Object data){
        messagingTemplate.convertAndSend("/sub/walk/" + email,  WebSocketResponse.ok(data));
    }

    private void sendMessagetoWalkRequestUrl(String email, Object data){
        messagingTemplate.convertAndSend("/sub/walk/request" + email,  WebSocketResponse.ok(data));
    }
}
