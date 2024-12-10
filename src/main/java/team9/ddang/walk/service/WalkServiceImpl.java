package team9.ddang.walk.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import team9.ddang.dog.entity.Dog;
import team9.ddang.dog.entity.MemberDog;
import team9.ddang.dog.repository.MemberDogRepository;
import team9.ddang.global.service.RedisService;
import team9.ddang.global.service.S3Service;
import team9.ddang.member.entity.Member;
import team9.ddang.member.entity.WalkWithMember;
import team9.ddang.member.repository.WalkWithMemberRepository;
import team9.ddang.walk.entity.Location;
import team9.ddang.walk.entity.Walk;
import team9.ddang.walk.entity.WalkDog;
import team9.ddang.walk.repository.LocationBulkRepository;
import team9.ddang.walk.repository.WalkDogRepository;
import team9.ddang.walk.repository.WalkRepository;
import team9.ddang.walk.service.request.walk.CompleteWalkServiceRequest;
import team9.ddang.walk.service.response.walk.CompleteWalkResponse;
import team9.ddang.walk.service.response.walk.WalkWithDogInfo;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static team9.ddang.walk.exception.WalkExceptionMessage.*;
import static team9.ddang.walk.service.RedisKey.*;
import static team9.ddang.walk.util.WalkCalculator.calculateCalorie;

@Service
@RequiredArgsConstructor
public class WalkServiceImpl implements WalkService{

    private final RedisService redisService;
    private final WalkRepository walkRepository;
    private final MemberDogRepository memberDogRepository;
    private final WalkDogRepository walkDogRepository;
    private final LocationBulkRepository locationBulkRepository;
    private final WalkWithMemberRepository walkWithMemberRepository;
    private final S3Service s3Service;

    private final static String WALK_ROUTE_DIR = "walk";

    @Override
    @Transactional
    public CompleteWalkResponse completeWalk(Member member, CompleteWalkServiceRequest completeWalkServiceRequest,
                                             MultipartFile walkImgFile) throws IOException {
        Dog dog = getDogFromMemberId(member.getMemberId());
        List<Location> locations = getLocationList(member.getEmail());
        String walkImg = s3Service.upload(walkImgFile, WALK_ROUTE_DIR);

        if(locations.isEmpty()) {
            throw new IllegalArgumentException(ABNORMAL_WALK.getText());
        }

        Walk walk = completeWalkServiceRequest.toEntity(locations, member, walkImg);
        saveWalkAndLocationAndDog(locations, walk, dog);

        deleteRedisData(member.getEmail());

        return CompleteWalkResponse.of(
                member.getName(), dog.getName(), walk.getTotalDistance(), completeWalkServiceRequest.totalWalkTime(),
                calculateCalorie(dog.getWeight(),completeWalkServiceRequest.totalDistance()),
                walk.getWalkImg(), getWalkingFriendInfo(member)
        );
    }

    private void saveWalkAndLocationAndDog(List<Location> locations, Walk walk, Dog dog){
        locations.forEach(location -> location.updateWalk(walk));

        WalkDog walkDog = WalkDog.builder()
                .dog(dog)
                .walk(walk)
                .build();

        dog.doWalk();
        walkRepository.save(walk);
        walkDogRepository.save(walkDog);
        locationBulkRepository.saveAll(locations);
    }

    private List<String> getListFromRedis(String email) {
        String key = LIST_KEY + email;
        List<String> locations = redisService.getStringListOpsValues(key);
        redisService.deleteValues(key);
        return locations;
    }

    private List<Location> getLocationList(String email){
        List<String> locations = getListFromRedis(email);

        return locations != null && !locations.isEmpty() ?
                locations.stream().map(this::getLocationTempFromString).toList() : Collections.emptyList();
    }

    private Location getLocationTempFromString(String location){
        String[] parts = location.split(",");
        double longitude = Double.parseDouble(parts[0].split("=")[1]);
        double latitude = Double.parseDouble(parts[1].split("=")[1]);
        LocalDateTime timeStamp = LocalDateTime.parse(parts[2].split("=")[1]);

        return Location.createTemp(longitude, latitude, timeStamp);
    }

    private Dog getDogFromMemberId(Long memberId){
        MemberDog memberDog = memberDogRepository.findMemberDogByMemberId(memberId)
                .orElseThrow(() -> new IllegalArgumentException(DOG_NOT_FOUND.getText()));
        return memberDog.getDog();
    }

    private WalkWithDogInfo getWalkingFriendInfo(Member member){
        String key = WALK_WITH_KEY + member.getEmail();
        if(redisService.checkHasKey(key)){
            String otherEmail = redisService.getValues(key);

            MemberDog otherMemberDog = memberDogRepository.findMemberDogByMemberEmail(otherEmail)
                    .orElseThrow(() -> new IllegalArgumentException(DOG_NOT_FOUND.getText()));

            saveWalkWithMember(member, otherMemberDog.getMember());

            redisService.deleteValues(key);
            return WalkWithDogInfo.of(otherMemberDog.getMember(), otherMemberDog.getDog());
        }

        return null;
    }

    private void saveWalkWithMember(Member member, Member otherMember){
        WalkWithMember walkWithMember = WalkWithMember.builder()
                .sender(member)
                .receiver(otherMember)
                .build();

        walkWithMemberRepository.save(walkWithMember);
    }

    private void deleteRedisData(String email){
        if(redisService.checkHasKey(PROPOSAL_KEY + email)){
            redisService.deleteValues(PROPOSAL_KEY + email);
        }

        redisService.deleteGeoValues(POINT_KEY, email);

    }


}
