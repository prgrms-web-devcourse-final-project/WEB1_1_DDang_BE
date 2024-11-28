package team9.ddang.walk.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import team9.ddang.dog.entity.Dog;
import team9.ddang.dog.repository.MemberDogRepository;
import team9.ddang.member.entity.Member;
import team9.ddang.walk.entity.Walk;
import team9.ddang.walk.repository.WalkDogRepository;

import java.time.LocalDate;
import java.util.List;

import static team9.ddang.walk.exception.WalkExceptionMessage.DOG_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class WalkLogServiceImpl implements WalkLogService{

    private final MemberDogRepository memberDogRepository;
    private final WalkDogRepository walkDogRepository;

    @Override
    public List<LocalDate> getWalkLogs(Member member) {
        Dog dog = getDogFromMemberId(member.getMemberId());
        List<Walk> walkList = walkDogRepository.findAllWalksByDog(dog);

        return walkList.stream().map(walk -> walk.getStartTime().toLocalDate()).toList();
    }

    private Dog getDogFromMemberId(Long memberId){
        return memberDogRepository.findMemberDogByMemberId(memberId)
                .orElseThrow(() -> new IllegalArgumentException(DOG_NOT_FOUND.getText())).getDog();
    }
}
