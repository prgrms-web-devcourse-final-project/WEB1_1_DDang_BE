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
import team9.ddang.member.entity.IsMatched;
import team9.ddang.member.entity.Member;
import team9.ddang.walk.service.request.AcceptWalkServiceRequest;
import team9.ddang.walk.service.request.ProposalWalkServiceRequest;
import team9.ddang.walk.service.request.StartWalkServiceRequest;
import team9.ddang.walk.service.response.MemberNearbyInfo;
import team9.ddang.walk.service.response.MemberNearbyResponse;
import team9.ddang.walk.service.response.ProposalWalkResponse;

import java.util.ArrayList;
import java.util.List;

import static team9.ddang.walk.service.RedisKey.LIST_KEY;
import static team9.ddang.walk.service.RedisKey.POINT_KEY;

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
                .orElseThrow().getDog();

        messagingTemplate.convertAndSend("/sub/walk/" + proposalWalkServiceRequest.otherMemberEmail(),
                ProposalWalkResponse.of(dog, member, proposalWalkServiceRequest.comment()));
    }

    @Override
    public void acceptWalk(Member member, AcceptWalkServiceRequest service) {

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
        messagingTemplate.convertAndSend("/sub/walk/" + email,
                memberNearbyInfos.stream()
                .filter(memberNearbyInfo -> memberNearbyInfo.isMatched().equals(IsMatched.TRUE))
                .map(MemberNearbyResponse::from).toList());
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
}
