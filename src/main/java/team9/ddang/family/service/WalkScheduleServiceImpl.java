package team9.ddang.family.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team9.ddang.dog.entity.Dog;
import team9.ddang.dog.entity.MemberDog;
import team9.ddang.dog.repository.DogRepository;
import team9.ddang.dog.repository.MemberDogRepository;
import team9.ddang.family.entity.Family;
import team9.ddang.family.entity.WalkSchedule;
import team9.ddang.family.exception.FamilyExceptionMessage;
import team9.ddang.family.repository.FamilyRepository;
import team9.ddang.family.repository.WalkScheduleRepository;
import team9.ddang.family.service.request.WalkScheduleCreateServiceRequest;
import team9.ddang.family.service.response.WalkScheduleResponse;
import team9.ddang.member.entity.Member;
import team9.ddang.member.repository.MemberRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class WalkScheduleServiceImpl implements WalkScheduleService {

    private final WalkScheduleRepository walkScheduleRepository;
    private final FamilyRepository familyRepository;
    private final MemberRepository memberRepository;
    private final DogRepository dogRepository;
    private final MemberDogRepository memberDogRepository;

    @Override
    @Transactional
    public WalkScheduleResponse createWalkSchedule (WalkScheduleCreateServiceRequest request, Member member){

        Member currentMember = findMemberByIdOrThrowException(member.getMemberId());

        Member walkMember = findMemberByIdOrThrowException(request.memberId());

        if (currentMember.getFamily() == null) {
            throw new IllegalArgumentException(FamilyExceptionMessage.MEMBER_NOT_IN_FAMILY.getText());
        }

        if (walkMember.getFamily() == null) {
            throw new IllegalArgumentException(FamilyExceptionMessage.MEMBER_NOT_IN_FAMILY.getText());
        }

        if(!currentMember.getFamily().getFamilyId().equals(walkMember.getFamily().getFamilyId())){
            throw new IllegalArgumentException(FamilyExceptionMessage.MEMBER_NOT_EQUAL_FAMILY.getText());
        }

        Family family = currentMember.getFamily();

        Long dogId = findMemberDogByIdOrThrowException(currentMember.getMemberId()).getDog().getDogId();

        if(request.dogId() != dogId) {
            throw new IllegalArgumentException(FamilyExceptionMessage.DOG_NOT_CAST.getText());
        }

        Dog dog = findDogByIdOrThrowException(dogId);

        if(!family.getFamilyId().equals(dog.getFamily().getFamilyId())){
            throw new IllegalArgumentException(FamilyExceptionMessage.DOG_NOT_IN_FAMILY.getText());
        }

        WalkSchedule walkSchedule = WalkSchedule.builder()
                .member(walkMember)
                .dog(dog)
                .dayOfWeek(request.dayOfWeek())
                .walkTime(request.walkTime())
                .family(family)
                .build();

        walkScheduleRepository.save(walkSchedule);

        return WalkScheduleResponse.from(walkSchedule);
    }

    private Family findFamilyByIdOrThrowException(Long id) {
        return familyRepository.findActiveById(id)
                .orElseThrow(() -> {
                    log.warn(">>>> {} : {} <<<<", id, FamilyExceptionMessage.FAMILY_NOT_FOUND);
                    return new IllegalArgumentException(FamilyExceptionMessage.FAMILY_NOT_FOUND.getText());
                });
    }

    private Member findMemberByIdOrThrowException(Long id) {
        return memberRepository.findActiveById(id)
                .orElseThrow(() -> {
                    log.warn(">>>> {} : {} <<<<", id, FamilyExceptionMessage.MEMBER_NOT_FOUND);
                    return new IllegalArgumentException(FamilyExceptionMessage.MEMBER_NOT_FOUND.getText());
                });
    }

    private MemberDog findMemberDogByIdOrThrowException(Long id) {
        return memberDogRepository.findMemberDogByMemberId(id)
                .orElseThrow(() -> {
                    log.warn(">>>> {} : {} <<<<", id, FamilyExceptionMessage.MEMBER_DOG_NOT_FOUND);
                    return new IllegalArgumentException(FamilyExceptionMessage.MEMBER_DOG_NOT_FOUND.getText());
                });
    }

    private Dog findDogByIdOrThrowException(Long id) {
        return dogRepository.findActiveById(id)
                .orElseThrow(() -> {
                    log.warn(">>>> {} : {} <<<<", id, FamilyExceptionMessage.DOG_NOT_FOUND);
                    return new IllegalArgumentException(FamilyExceptionMessage.DOG_NOT_FOUND.getText());
                });
    }
}
