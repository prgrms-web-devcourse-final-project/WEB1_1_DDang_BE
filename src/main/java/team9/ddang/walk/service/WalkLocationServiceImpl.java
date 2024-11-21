package team9.ddang.walk.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.geo.Circle;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.GeoResults;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.GeoOperations;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team9.ddang.walk.service.request.LocationServiceRequest;

import java.util.List;

import static team9.ddang.walk.service.RedisKey.LIST_KEY;
import static team9.ddang.walk.service.RedisKey.POINT_KEY;

@Service
@RequiredArgsConstructor
public class WalkLocationServiceImpl implements WalkLocationService {

    private final RedisTemplate redisTemplate;


    @Override
    @Transactional
    public void startWalk(String email, LocationServiceRequest locationServiceRequest){
        saveMemberLocation(email,locationServiceRequest);
    }

    private void saveMemberLocation(String email, LocationServiceRequest locationServiceRequest){
        GeoOperations<String, String> geoOperations = redisTemplate.opsForGeo(); // redis 버전 3.2 이상 아니면 오류발생
        ListOperations<String, String> listOperations = redisTemplate.opsForList();

        // 좌표 데이터를 String 변환
        String pointData = locationServiceRequest.toStringFormat();
        Point point = new Point(locationServiceRequest.longitude(), locationServiceRequest.latitude());

        listOperations.rightPush(LIST_KEY + email, pointData);
        geoOperations.add(POINT_KEY, point, email);
    }

    private void findNearbyMember(String email){

        Point memberLocation = findMemberLocation(email);

        Distance radius = new Distance(200, RedisGeoCommands.DistanceUnit.METERS);
        Circle circle = new Circle(memberLocation, radius);

        GeoOperations<String, String> geoOperations = redisTemplate.opsForGeo();

        RedisGeoCommands.GeoRadiusCommandArgs args = RedisGeoCommands.GeoRadiusCommandArgs
                .newGeoRadiusArgs()
                .includeDistance()
                .includeCoordinates()
                .sortAscending()
                .limit(15);

        GeoResults<RedisGeoCommands.GeoLocation<String>> results = geoOperations
                .radius(POINT_KEY, circle, args);
        // TODO : 강번따 기능을 위해서 멤버 정보, 개 정보 등등 받아와야 함

    }

    private Point findMemberLocation(String email){
        List<Point> position = redisTemplate.opsForGeo().position(POINT_KEY, email);

        if(position != null && position.isEmpty()){
            throw new IllegalArgumentException("현재 멤버의 위치 정보가 존재하지 않음");
        }

        return position.get(0);
    }
}
