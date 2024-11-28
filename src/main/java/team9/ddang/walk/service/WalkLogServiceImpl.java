package team9.ddang.walk.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import team9.ddang.dog.entity.Dog;
import team9.ddang.dog.repository.MemberDogRepository;
import team9.ddang.member.entity.Member;
import team9.ddang.walk.entity.Position;
import team9.ddang.walk.entity.Walk;
import team9.ddang.walk.entity.WalkDog;
import team9.ddang.walk.repository.LocationRepository;
import team9.ddang.walk.repository.WalkDogRepository;
import team9.ddang.walk.repository.WalkRepository;
import team9.ddang.walk.service.request.log.GetLogByDateServiceRequest;
import team9.ddang.walk.service.response.log.WalkLogResponse;
import team9.ddang.walk.util.WalkCalculator;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static team9.ddang.walk.exception.WalkExceptionMessage.DOG_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class WalkLogServiceImpl implements WalkLogService{

    private final MemberDogRepository memberDogRepository;
    private final WalkDogRepository walkDogRepository;
    private final WalkRepository walkRepository;
    private final LocationRepository locationRepository;

    @Override
    public List<LocalDate> getWalkLogs(Member member) {
        Dog dog = getDogFromMemberId(member.getMemberId());
        List<WalkDog> walkList = walkDogRepository.findAllByDog_DogId(dog.getDogId());

        return walkList.stream().map(walk -> walk.getCreatedAt().toLocalDate()).toList();
    }

    @Override
    public List<WalkLogResponse> getWalkLogByDate(Member member, GetLogByDateServiceRequest service) {
        List<Walk> walks = walkRepository.findAllByMemberAndDate(member.getMemberId(), service.date());
        Dog dog  = getDogFromMemberId(member.getMemberId());

        return walks.stream().map(walk ->
        { List<Position> positionList = locationRepository.findAllPositionByWalkId(walk.getWalkId());
            return WalkLogResponse.of( positionList, walk, WalkCalculator.calculateCalorie(dog.getWeight(), walk.getTotalDistance()) ); })
                .toList();
    }

    private Dog getDogFromMemberId(Long memberId){
        return memberDogRepository.findMemberDogByMemberId(memberId)
                .orElseThrow(() -> new IllegalArgumentException(DOG_NOT_FOUND.getText())).getDog();
    }
}
