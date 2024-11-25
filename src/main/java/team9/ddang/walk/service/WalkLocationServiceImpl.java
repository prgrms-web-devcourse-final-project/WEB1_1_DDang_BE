package team9.ddang.walk.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.geo.*;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.GeoOperations;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team9.ddang.dog.entity.Dog;
import team9.ddang.dog.repository.MemberDogRepository;
import team9.ddang.global.api.WebSocketResponse;
import team9.ddang.member.entity.IsMatched;
import team9.ddang.member.entity.Member;
import team9.ddang.walk.service.request.DecisionWalkServiceRequest;
import team9.ddang.walk.service.request.ProposalWalkServiceRequest;
import team9.ddang.walk.service.request.StartWalkServiceRequest;
import team9.ddang.walk.service.response.MemberNearbyInfo;
import team9.ddang.walk.service.response.MemberNearbyResponse;
import team9.ddang.walk.service.response.ProposalWalkResponse;
import team9.ddang.walk.service.response.WalkWithResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static team9.ddang.walk.service.RedisKey.*;

@Service
@RequiredArgsConstructor
public class WalkLocationServiceImpl implements WalkLocationService {

    private final RedisTemplate redisTemplate;
    private final MemberDogRepository memberDogRepository;
    private final SimpMessagingTemplate messagingTemplate;


    @Override
    @Transactional
    public void startWalk(String email, StartWalkServiceRequest startWalkServiceRequest){
        saveMemberLocation(email, startWalkServiceRequest);
        findNearbyMember(email);
    }

    @Override
    public void proposalWalk(Member member, ProposalWalkServiceRequest proposalWalkServiceRequest) {
        Dog dog = memberDogRepository.findMemberDogByMemberId(member.getMemberId())
                .orElseThrow(() -> new IllegalArgumentException("개가 존재하지 않습니다.")).getDog();
        String otherEmail = proposalWalkServiceRequest.otherMemberEmail();

        if(redisTemplate.opsForValue().get(PROPOSAL_KEY + member.getEmail()) != null){
            throw new IllegalArgumentException("이미 다른 견주분에게 산책을 제안을 하신 상태 입니다.");
        }

        redisTemplate.opsForValue().set(PROPOSAL_KEY + member.getEmail(), otherEmail, 3, TimeUnit.MINUTES);
        ProposalWalkResponse response = ProposalWalkResponse.of(dog, member, proposalWalkServiceRequest.comment());

        sendMessageToWalkUrl(otherEmail, response);
        sendMessageToWalkUrl(member.getEmail(), response);
    }

    @Override
    public void decisionWalk(Member member, DecisionWalkServiceRequest serviceRequest) {
        String memberEmail = (String) redisTemplate.opsForValue().get(PROPOSAL_KEY + serviceRequest.otherEmail());
        if(memberEmail == null){
            throw new IllegalArgumentException("제안을 취소했거나 이미 강번따를 진행 중인 유저 입니다.");
        }

        if(!memberEmail.equals(member.getEmail())){
            throw new IllegalArgumentException("제안을 한 유저와 받은 유저가 일치하지 않습니다.");
        }
        redisTemplate.delete(PROPOSAL_KEY + serviceRequest.otherEmail());

        redisTemplate.opsForValue().set(WITH_WALK_KEY + member.getEmail(), serviceRequest.otherEmail());
        redisTemplate.opsForValue().set(WITH_WALK_KEY + serviceRequest.otherEmail(), member.getEmail());

        sendMessagetoWalkRequestUrl(member.getEmail(), serviceRequest.decision());
        sendMessagetoWalkRequestUrl(serviceRequest.otherEmail(), serviceRequest.decision());
    }

    @Override
    public void startWalkWith(String email, StartWalkServiceRequest startWalkServiceRequest) {
        saveMemberLocation(email, startWalkServiceRequest);
        String otherMemberEmail = (String) redisTemplate.opsForValue().get(WITH_WALK_KEY + email);

        if(otherMemberEmail == null){
            throw new IllegalArgumentException("상대 이메일 정보가 존재하지 않습니다.");
        }

        sendMessageToWalkUrl(otherMemberEmail, WalkWithResponse.of(email, startWalkServiceRequest));
    }

    private void saveMemberLocation(String email, StartWalkServiceRequest startWalkServiceRequest){
        GeoOperations<String, String> geoOperations = redisTemplate.opsForGeo(); // redis 버전 3.2 이상 아니면 오류발생
        ListOperations<String, String> listOperations = redisTemplate.opsForList();

        // 좌표 데이터를 String 변환
        String pointData = startWalkServiceRequest.toStringFormat();
        Point point = new Point(startWalkServiceRequest.longitude(), startWalkServiceRequest.latitude());

        listOperations.rightPush(LIST_KEY + email, pointData);
        geoOperations.add(POINT_KEY, point, email);
    }

    private void findNearbyMember(String email){
        Point memberLocation = findMemberLocation(email);
        GeoResults<RedisGeoCommands.GeoLocation<String>> results = getNearbyMemberResultsFromRedis(memberLocation);
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

    private Point findMemberLocation(String email){
        List<Point> position = redisTemplate.opsForGeo().position(POINT_KEY, email);

        if(position != null && position.isEmpty()){
            throw new IllegalArgumentException("현재 멤버의 위치 정보가 존재하지 않음");
        }

        return position.get(0);
    }

    private GeoResults<RedisGeoCommands.GeoLocation<String>> getNearbyMemberResultsFromRedis(Point memberLocation){
        Distance radius = new Distance(200, RedisGeoCommands.DistanceUnit.METERS);
        Circle circle = new Circle(memberLocation, radius);

        GeoOperations<String, String> geoOperations = redisTemplate.opsForGeo();

        RedisGeoCommands.GeoRadiusCommandArgs args = RedisGeoCommands.GeoRadiusCommandArgs
                .newGeoRadiusArgs()
                .includeDistance()
                .includeCoordinates()
                .sortAscending()
                .limit(10);

        return geoOperations.radius(POINT_KEY, circle, args);
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