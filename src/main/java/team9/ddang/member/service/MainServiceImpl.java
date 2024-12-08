package team9.ddang.member.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team9.ddang.dog.entity.Dog;
import team9.ddang.dog.repository.MemberDogRepository;
import team9.ddang.family.entity.WalkSchedule;
import team9.ddang.family.repository.WalkScheduleRepository;
import team9.ddang.member.entity.Member;
import team9.ddang.member.service.response.MainResponse;
import team9.ddang.walk.entity.Walk;
import team9.ddang.walk.repository.WalkDogRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class MainServiceImpl implements MainService{

    private final WalkScheduleRepository walkScheduleRepository;
    private final MemberDogRepository memberDogRepository;
    private final WalkDogRepository walkDogRepository;

    @Override
    @Transactional(readOnly = true)
    public MainResponse getMain(Member member) {
        Dog dog = getDogFromMemberId(member.getMemberId());
        Member walkMember = getTodayWalkMemberByDogId(dog.getDogId());
        Map<String, Long> walkSummary = calculateWalkSummary(dog.getDogId());

        long totalSeconds = walkSummary.get("totalSeconds");
        long totalDistanceMeter = walkSummary.get("totalDistanceMeter");

        if (walkMember == null) {
            return MainResponse.of(dog, totalSeconds, (int) totalDistanceMeter, member);
        }

        return MainResponse.of(walkMember, dog, totalSeconds, (int) totalDistanceMeter, member);
    }

    private Map<String, Long> calculateWalkSummary(Long dogId) {
        long totalSeconds = 0;
        long totalDistanceMeter = 0;
        List<Walk> walkList = findWalksByDogIdAndToday(dogId);

        for (Walk walk : walkList) {
            totalSeconds += ChronoUnit.SECONDS.between(walk.getStartTime(), walk.getEndTime());
            totalDistanceMeter += walk.getTotalDistance();
        }

        Map<String, Long> summary = new HashMap<>();
        summary.put("totalSeconds", totalSeconds);
        summary.put("totalDistanceMeter", totalDistanceMeter);
        return summary;
    }

    private Dog getDogFromMemberId(Long memberId){
        return memberDogRepository.findMemberDogByMemberId(memberId)
                .orElseThrow(() -> new IllegalArgumentException("소유한 강아지를 찾을 수 없습니다.")).getDog();
    }

    private List<Walk> findWalksByDogIdAndToday(Long dogId) {
        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.plusDays(1).atStartOfDay().minusNanos(1);
        return walkDogRepository.findWalksByDogIdAndToday(dogId, startOfDay, endOfDay);
    }

    private Member getTodayWalkMemberByDogId(long dogId){
        Member walkMember = null;
        String today = LocalDate.now().getDayOfWeek().name();

        List<WalkSchedule> walkScheduleList = walkScheduleRepository.findAllByDogId(dogId);
        for(WalkSchedule walkSchedule : walkScheduleList){
            if(today.startsWith(walkSchedule.getDayOfWeek().name())){
                walkMember = walkSchedule.getMember();
            }
        }

        return walkMember;
    }
}
