package team9.ddang.walk.service;

import jakarta.validation.constraints.Email;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team9.ddang.dog.entity.Dog;
import team9.ddang.dog.entity.MemberDog;
import team9.ddang.dog.repository.MemberDogRepository;
import team9.ddang.global.service.RedisService;
import team9.ddang.member.entity.Member;
import team9.ddang.member.entity.WalkWithMember;
import team9.ddang.member.repository.MemberRepository;
import team9.ddang.member.repository.WalkWithMemberRepository;
import team9.ddang.walk.entity.Location;
import team9.ddang.walk.entity.Walk;
import team9.ddang.walk.entity.WalkDog;
import team9.ddang.walk.repository.LocationBulkRepository;
import team9.ddang.walk.repository.WalkDogRepository;
import team9.ddang.walk.repository.WalkRepository;
import team9.ddang.walk.service.request.CompleteWalkServiceRequest;
import team9.ddang.walk.service.response.CompleteWalkResponse;
import team9.ddang.walk.service.response.WalkWithDogInfo;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

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
    private final MemberRepository memberRepository;
    private final WalkWithMemberRepository walkWithMemberRepository;


    @Override
    @Transactional
    public CompleteWalkResponse completeWalk(Long memberId, CompleteWalkServiceRequest completeWalkServiceRequest) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow();
        // TODO : security로 멤버 받아올 예정
        Dog dog = getDogFromMemberId(memberId);
        List<Location> locations = getLocationList(member.getEmail());

        if(locations.isEmpty()) {
            throw new IllegalArgumentException("산책이 정상적으로 이루어지지 않았습니다.");
        }

        Walk walk = completeWalkServiceRequest.toEntity(locations, member);
        saveWalkAndLocationAndDog(locations, walk, dog);

        redisService.deleteGeoValues(POINT_KEY, member.getEmail());

        return CompleteWalkResponse.of(
                member.getName(), dog.getName(), walk.getTotalDistance(), completeWalkServiceRequest.totalWalkTime(),
                calculateCalorie(dog.getWeight(),completeWalkServiceRequest.totalDistance()),
                locations.stream().map(Location::getPosition).toList(), getWalkingFriendInfo(member)
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
                .orElseThrow(() -> new IllegalArgumentException("멤버가 소유하고 있는 개가 없습니다!"));
        return memberDog.getDog();
    }

    private WalkWithDogInfo getWalkingFriendInfo(Member member){
        String key = WALK_WITH_KEY + member.getEmail();
        if(redisService.checkHasKey(key)){
            String otherEmail = redisService.getValues(key);

            MemberDog otherMemberDog = memberDogRepository.findMemberDogByMemberEmail(otherEmail)
                    .orElseThrow(() -> new IllegalArgumentException("멤버가 소유하고 있는 개가 없습니다."));

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


}
