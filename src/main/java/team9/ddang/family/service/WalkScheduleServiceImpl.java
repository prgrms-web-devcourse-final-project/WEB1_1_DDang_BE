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
import team9.ddang.family.service.request.WalkScheduleDeleteServiceRequest;
import team9.ddang.family.service.response.WalkScheduleResponse;
import team9.ddang.member.entity.Member;
import team9.ddang.member.repository.MemberRepository;

import java.util.List;
import java.util.stream.Collectors;

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
    public List<WalkScheduleResponse> createWalkSchedule (WalkScheduleCreateServiceRequest request, Member member){

        Member currentMember = validateMemberInFamily(member);

        Family family = currentMember.getFamily();

        // TODO : 나중에 여러 강아지를 키울 수 있게 된다면 강아지를 리스트로 받아와야 할 듯
        Dog dog = findDogByFamilyIdOrThrowException(currentMember.getFamily().getFamilyId());

        List<WalkSchedule> walkSchedules = request.dayOfWeek().stream()
                .map(dayOfWeek -> WalkSchedule.builder()
                        .member(currentMember)
                        .dog(dog)
                        .dayOfWeek(dayOfWeek)
                        .walkTime(request.walkTime())
                        .family(family)
                        .build())
                .toList();

        walkScheduleRepository.saveAll(walkSchedules);

        return walkSchedules.stream()
                .map(WalkScheduleResponse::from)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<WalkScheduleResponse> getWalkSchedulesByFamilyId(Member member) {

        Member currentMember = validateMemberInFamily(member);

        List<WalkSchedule> schedules = walkScheduleRepository.findAllByFamilyIdWithDetails(currentMember.getFamily().getFamilyId());

        return schedules.stream()
                .map(WalkScheduleResponse::from)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<WalkScheduleResponse> getWalkSchedulesByMemberId(Long memberId, Member member){
        validateMemberInFamily(member);

        Member searchMember = findMemberByIdOrThrowException(memberId);

        if (searchMember.getFamily() == null) {
            throw new IllegalArgumentException(FamilyExceptionMessage.MEMBER_NOT_IN_FAMILY.getText());
        }

        List<WalkSchedule> schedules = walkScheduleRepository.findAllByMemberId(searchMember.getMemberId());

        return schedules.stream()
                .map(WalkScheduleResponse::from)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteWalkSchedule(WalkScheduleDeleteServiceRequest request, Member member) {

        Member currentMember = validateMemberInFamily(member);

        List<WalkSchedule> walkSchedules = walkScheduleRepository.findAllById(request.walkScheduleId());

        for (WalkSchedule walkSchedule : walkSchedules) {
            if (!walkSchedule.getMember().getMemberId().equals(currentMember.getMemberId())) {
                throw new IllegalArgumentException(FamilyExceptionMessage.WALKSCHEDULE_NOT_IN_FAMILY.getText());
            }
        }

        walkScheduleRepository.deleteAllById(request.walkScheduleId());
    }


    private Member validateMemberInFamily(Member member) {
        Member currentMember = findMemberByIdOrThrowException(member.getMemberId());
        if (currentMember.getFamily() == null) {
            throw new IllegalArgumentException(FamilyExceptionMessage.MEMBER_NOT_IN_FAMILY.getText());
        }
        return currentMember;
    }

    private Dog validateAndGetDog(Long dogId, Family family) {
        Dog dog = findDogByIdOrThrowException(dogId);
        if (!family.getFamilyId().equals(dog.getFamily().getFamilyId())) {
            throw new IllegalArgumentException(FamilyExceptionMessage.DOG_NOT_CAST.getText());
        }
        return dog;
    }

    private Family findFamilyByIdOrThrowException(Long id) {
        return familyRepository.findActiveById(id)
                .orElseThrow(() -> {
                    log.warn(">>>> {} : {} <<<<", id, FamilyExceptionMessage.FAMILY_NOT_FOUND);
                    return new IllegalArgumentException(FamilyExceptionMessage.FAMILY_NOT_FOUND.getText());
                });
    }

    private Dog findDogByFamilyIdOrThrowException(Long id) {
        return dogRepository.findActiveByFamilyId(id)
                .orElseThrow(() -> {
                    log.warn(">>>> {} : {} <<<<", id, FamilyExceptionMessage.MEMBER_DOG_NOT_FOUND);
                    return new IllegalArgumentException(FamilyExceptionMessage.MEMBER_DOG_NOT_FOUND.getText());
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

    private WalkSchedule findWalkScheduleByIdOrThrowException(Long id) {
        return walkScheduleRepository.findActiveById(id)
                .orElseThrow(() -> {
                    log.warn(">>>> {} : {} <<<<", id, FamilyExceptionMessage.WALKSCHEDULE_NOT_FOUND);
                    return new IllegalArgumentException(FamilyExceptionMessage.WALKSCHEDULE_NOT_FOUND.getText());
                });
    }
}
