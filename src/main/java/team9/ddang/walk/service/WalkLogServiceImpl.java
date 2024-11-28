package team9.ddang.walk.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team9.ddang.dog.entity.Dog;
import team9.ddang.dog.repository.MemberDogRepository;
import team9.ddang.member.entity.Member;
import team9.ddang.member.repository.MemberRepository;
import team9.ddang.walk.entity.Position;
import team9.ddang.walk.entity.Walk;
import team9.ddang.walk.entity.WalkDog;
import team9.ddang.walk.repository.LocationRepository;
import team9.ddang.walk.repository.WalkDogRepository;
import team9.ddang.walk.repository.WalkRepository;
import team9.ddang.walk.service.request.log.GetLogByDateServiceRequest;
import team9.ddang.walk.service.response.log.WalkLogByFamilyResponse;
import team9.ddang.walk.service.response.log.WalkLogResponse;
import team9.ddang.walk.service.response.log.WalkStaticsResponse;
import team9.ddang.walk.util.WalkCalculator;

import java.time.LocalDate;
import java.time.Year;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

import static team9.ddang.walk.exception.WalkExceptionMessage.DOG_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class WalkLogServiceImpl implements WalkLogService{

    private final MemberDogRepository memberDogRepository;
    private final MemberRepository memberRepository;
    private final WalkDogRepository walkDogRepository;
    private final WalkRepository walkRepository;
    private final LocationRepository locationRepository;

    @Override
    @Transactional(readOnly = true)
    public List<LocalDate> getWalkLogs(Member member) {
        Dog dog = getDogFromMemberId(member.getMemberId());
        List<WalkDog> walkList = walkDogRepository.findAllByDog_DogId(dog.getDogId());

        return walkList.stream().map(walk -> walk.getCreatedAt().toLocalDate()).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<WalkLogResponse> getWalkLogByDate(Member member, GetLogByDateServiceRequest service) {
        List<Walk> walks = walkRepository.findAllByMemberAndDate(member.getMemberId(), service.date());
        Dog dog  = getDogFromMemberId(member.getMemberId());

        return walks.stream().map(walk ->
        { List<Position> positionList = locationRepository.findAllPositionByWalkId(walk.getWalkId());
            return WalkLogResponse.of( positionList, walk, WalkCalculator.calculateCalorie(dog.getWeight(), walk.getTotalDistance()) ); })
                .toList();
    }

    @Transactional(readOnly = true)
    @Override
    public List<Integer> getYearlyWalkLog(Member member) {
        Dog dog = getDogFromMemberId(member.getMemberId());
        List<WalkDog> walkDogs = walkDogRepository.findWalkDogsByYearAndDogId(Year.now().getValue(), dog.getDogId());
        List<Integer> list = new ArrayList<>(Collections.nCopies(12,0));

        for(WalkDog walkDog : walkDogs){
            int index = walkDog.getCreatedAt().getMonthValue()-1;
            list.set(index, list.get(index)+1);
        }

        return list;
    }

    @Transactional(readOnly = true)
    @Override
    public List<WalkLogByFamilyResponse> getYearlyWalkLogByFamily(Member member) {
        List<Member> familyMemberList = memberRepository.findFamilyMembersByMemberId(member.getMemberId());
        List<Walk> walkList = walkRepository.findAllByMembersAndDate(familyMemberList, Year.now().getValue());

        Map<Member, Long> walkCountByMember = walkList.stream()
                .collect(Collectors.groupingBy(Walk::getMember, Collectors.counting()));

        List<WalkLogByFamilyResponse> responseList = new ArrayList<>(walkCountByMember.entrySet().stream()
                .map(entry -> new WalkLogByFamilyResponse(
                        entry.getKey().getMemberId(),
                        entry.getKey().getFamilyRole(),
                        entry.getValue().intValue()))
                .sorted(Comparator.comparingInt(WalkLogByFamilyResponse::count).reversed())
                .toList());

        // 로그인한 유저를 맨 앞에 배치
        responseList.sort(Comparator.comparing(response -> response.memberId().equals(member.getMemberId()) ? -1 : 1));

        return responseList;
    }

    @Transactional(readOnly = true)
    @Override
    public WalkStaticsResponse getTotalWalkLog(Member member) {
        List<Walk> walkList = walkDogRepository.findWalksByDogIdFromMemberId(member.getMemberId());
        long totalSeconds = 0;
        int totalDistanceMeter = 0;
        int totalWalkCount = walkList.size();
        for(Walk walk : walkList){
            totalSeconds += ChronoUnit.SECONDS.between(walk.getStartTime(), walk.getEndTime());
            totalDistanceMeter += walk.getTotalDistance();
        }

        return WalkStaticsResponse.of(totalSeconds, totalWalkCount, totalDistanceMeter/1000);
    }

    @Transactional(readOnly = true)
    @Override
    public WalkStaticsResponse getMonthlyTotalWalk(Member member) {
        List<Walk> walkList = walkDogRepository.findWalksByDogIdAndMonthFromMemberId(member.getMemberId(), LocalDate.now().getMonthValue());
        long totalSeconds = 0;
        int totalDistanceMeter = 0;
        int totalWalkCount = walkList.size();
        for(Walk walk : walkList){
            totalSeconds += ChronoUnit.SECONDS.between(walk.getStartTime(), walk.getEndTime());
            totalDistanceMeter += walk.getTotalDistance();
        }

        return WalkStaticsResponse.of(totalSeconds, totalWalkCount, totalDistanceMeter/1000);
    }

    private Dog getDogFromMemberId(Long memberId){
        return memberDogRepository.findMemberDogByMemberId(memberId)
                .orElseThrow(() -> new IllegalArgumentException(DOG_NOT_FOUND.getText())).getDog();
    }
}
