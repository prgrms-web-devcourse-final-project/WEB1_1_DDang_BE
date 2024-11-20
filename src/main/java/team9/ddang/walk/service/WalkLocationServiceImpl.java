package team9.ddang.walk.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.core.GeoOperations;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team9.ddang.walk.service.request.LocationServiceRequest;

@Service
@RequiredArgsConstructor
public class WalkLocationServiceImpl implements WalkLocationService {

    private final RedisTemplate redisTemplate;

    private static final String LIST_KEY = "geoPoints:";
    private static final String POINT_KEY = "geoPoint:";

    @Override
    @Transactional
    public void saveMemberLocation(Long memberId, LocationServiceRequest locationServiceRequest){
        GeoOperations<String, String> geoOperations = redisTemplate.opsForGeo(); // redis 버전 3.2 이상 아니면 오류발생
        ListOperations<String, String> listOperations = redisTemplate.opsForList();

        String listKey = LIST_KEY + memberId;
        String pointKey = POINT_KEY + memberId;
        // 좌표 데이터를 String 변환
        String pointData = locationServiceRequest.toStringFormat();
        Point point = new Point(locationServiceRequest.longitude(), locationServiceRequest.latitude());

        listOperations.rightPush(listKey, pointData);
        geoOperations.add(pointKey, point, String.valueOf(memberId));
    }
}
