package team9.ddang.family.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team9.ddang.dog.entity.Dog;
import team9.ddang.dog.entity.MemberDog;
import team9.ddang.dog.repository.DogRepository;
import team9.ddang.dog.repository.MemberDogRepository;
import team9.ddang.dog.service.response.GetDogResponse;
import team9.ddang.family.entity.Family;
import team9.ddang.family.exception.FamilyExceptionMessage;
import team9.ddang.family.repository.FamilyRepository;
import team9.ddang.family.repository.WalkScheduleRepository;
import team9.ddang.family.service.response.*;
import team9.ddang.member.entity.Member;
import team9.ddang.member.repository.MemberRepository;
import team9.ddang.walk.repository.WalkRepository;

import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

import static team9.ddang.walk.util.WalkCalculator.calculateCalorie;

@Slf4j
@Service
@RequiredArgsConstructor
public class FamilyServiceImpl implements FamilyService {

    private static final String REDIS_INVITE_KEY_PREFIX = "invite:";

    private final RedisTemplate<String, String> redisTemplate;
    private final WalkScheduleRepository walkScheduleRepository;
    private final FamilyRepository familyRepository;
    private final MemberRepository memberRepository;
    private final DogRepository dogRepository;
    private final MemberDogRepository memberDogRepository;
    private final WalkRepository walkRepository;

    @Override
    @Transactional
    public FamilyResponse createFamily(Member member) {

        Member currentMember = validateMemberNotInFamily(member);

        // TODO : 나중에 여러 강아지를 키울 수 있게 된다면 강아지를 리스트로 받아와야 할 듯
        Dog dog = findDogForMember(currentMember);

        Family family = Family.builder()
                .member(currentMember)
                .familyName("")
                .build();

        family = familyRepository.save(family);
        currentMember.updateFamily(family);
        dog.updateFamily(family);

        return new FamilyResponse(family);
    }

    @Override
    public InviteCodeResponse createInviteCode(Member member) {
        Member currentMember = validateMemberInFamily(member);

        Family family = currentMember.getFamily();
        String redisSearchKey = REDIS_INVITE_KEY_PREFIX;

        List<String> keys = Objects.requireNonNull(redisTemplate.keys(redisSearchKey + "*")).stream().toList();
        for (String key : keys) {
            String familyIdStr = redisTemplate.opsForValue().get(key);
            if (familyIdStr != null && familyIdStr.equals(String.valueOf(family.getFamilyId()))) {
                Long ttl = redisTemplate.getExpire(key);
                if (ttl != null && ttl > 0) {
                    String existingInviteCode = key.replace(redisSearchKey, "");
                    return new InviteCodeResponse(family, existingInviteCode, ttl);
                }
            }
        }

        String newInviteCode = generateInviteCode(family.getFamilyId());
        redisTemplate.opsForValue().set(REDIS_INVITE_KEY_PREFIX + newInviteCode, String.valueOf(family.getFamilyId()), Duration.ofMinutes(5));

        return new InviteCodeResponse(family, newInviteCode, Duration.ofMinutes(5).toSeconds());
    }


    @Override
    @Transactional
    public FamilyResponse addMemberToFamily(String inviteCode, Member member) {
        Member currentMember = validateMemberNotInFamily(member);
        validateMemberWithoutDog(member);
        Family family = getFamilyByInviteCode(inviteCode);
        addMemberToFamilyAssociations(currentMember, family);
        return new FamilyResponse(family);
    }

    @Override
    @Transactional
    public List<FamilyDogResponse> getFamilyDogs(String inviteCode, Member member) {
        findMemberByIdOrThrowException(member.getMemberId());
        Family family = getFamilyByInviteCode(inviteCode);
        return dogRepository.findAllByFamilyId(family.getFamilyId())
                .stream()
                .map(FamilyDogResponse::new)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public FamilyDetailResponse getMyFamily(Member member) {
        Member currentMember = validateMemberInFamily(member);
        Family family = currentMember.getFamily();

        List<GetDogResponse> dogs = dogRepository.findAllByFamilyId(family.getFamilyId())
                .stream()
                .map(dog -> new GetDogResponse(
                        dog.getDogId(),
                        dog.getName(),
                        dog.getBreed(),
                        dog.getBirthDate(),
                        dog.getWeight(),
                        dog.getGender(),
                        dog.getProfileImg(),
                        dog.getIsNeutered(),
                        family.getFamilyId(),
                        dog.getComment()
                ))
                .collect(Collectors.toList());

        List<MemberInfo> members = memberRepository.findAllByFamilyId(family.getFamilyId())
                .stream()
                .map(memberEntity -> {
                    List<WalkScheduleInfo> walkScheduleInfoList = walkScheduleRepository.findAllByMemberId(memberEntity.getMemberId())
                            .stream()
                            .map(schedule -> new WalkScheduleInfo(
                                    schedule.getWalkScheduleId(),
                                    schedule.getDayOfWeek(),
                                    schedule.getWalkTime()
                            ))
                            .collect(Collectors.toList());

                    int totalWalkCount = walkRepository.countWalksByMemberId(memberEntity.getMemberId());

                    return new MemberInfo(memberEntity, walkScheduleInfoList, totalWalkCount);
                })
                .collect(Collectors.toList());

        int totalWalkCount = walkRepository.countWalksByFamilyId(family.getFamilyId()); // 산책 횟수
        int totalDistanceInMeters = walkRepository.findTotalDistanceByFamilyId(family.getFamilyId()); // 총 산책 거리
        double totalDistanceInKilometers = totalDistanceInMeters / 1000.0;
        int totalCalorie = 0;
        if(!dogs.isEmpty()){
            totalCalorie = calculateCalorie(dogs.get(0).weight(), totalDistanceInMeters);
        }

        // TODO 강아지 유효성 검사 다시 한 번 생각해볼 것
        return new FamilyDetailResponse(family, members, dogs, totalWalkCount, totalDistanceInKilometers, totalCalorie);
    }

    @Override
    @Transactional
    public void removeMemberFromFamily(Long memberIdToRemove, Member member) {
        Member currentMember = validateFamilyBoss(member);
        validateMemberNotBoss(memberIdToRemove, currentMember);
        Member memberToRemove = validateMemberInSameFamily(memberIdToRemove, currentMember.getFamily());

        walkScheduleRepository.deleteByMemberId(memberToRemove.getMemberId());
        memberDogRepository.softDeleteByMember(memberToRemove);
        memberToRemove.updateFamily(null);
    }

    @Override
    @Transactional
    public void leaveFamily(Member member) {
        Member currentMember = validateMemberInFamily(member);
        validateNotFamilyBossForLeaving(currentMember);

        walkScheduleRepository.deleteByMemberId(currentMember.getMemberId());
        memberDogRepository.softDeleteByMember(currentMember);
        currentMember.updateFamily(null);
    }

    @Override
    @Transactional
    public void deleteFamily(Member member) {
        Member currentMember = validateFamilyBoss(member);
        Family family = currentMember.getFamily();
        validateFamilyNotEmpty(family);

        walkScheduleRepository.deleteByFamilyId(family.getFamilyId());
        familyRepository.softDeleteFamilyById(family.getFamilyId());
    }

    private Member validateMemberNotInFamily(Member member) {
        Member currentMember = findMemberByIdOrThrowException(member.getMemberId());
        if (currentMember.getFamily() != null) {
            throw new IllegalArgumentException(FamilyExceptionMessage.MEMBER_ALREADY_IN_FAMILY.getText());
        }
        return currentMember;
    }

    private Member validateMemberInFamily(Member member) {
        Member currentMember = findMemberByIdOrThrowException(member.getMemberId());
        if (currentMember.getFamily() == null) {
            throw new IllegalArgumentException(FamilyExceptionMessage.MEMBER_NOT_IN_FAMILY.getText());
        }
        return currentMember;
    }

    private Member validateFamilyBoss(Member member) {
        Member currentMember = validateMemberInFamily(member);
        if (!currentMember.getFamily().getMember().getMemberId().equals(currentMember.getMemberId())) {
            throw new IllegalArgumentException(FamilyExceptionMessage.MEMBER_NOT_FAMILY_BOSS.getText());
        }
        return currentMember;
    }
    private void validateMemberNotBoss(Long memberId, Member currentMember) {
        if (memberId.equals(currentMember.getMemberId())) {
            throw new IllegalArgumentException(FamilyExceptionMessage.MEMBER_FAMILY_BOSS.getText());
        }
    }

    private Member validateMemberInSameFamily(Long memberId, Family family) {
        Member member = findMemberByIdOrThrowException(memberId);
        if (member.getFamily() == null || !member.getFamily().getFamilyId().equals(family.getFamilyId())) {
            throw new IllegalArgumentException(FamilyExceptionMessage.MEMBER_NOT_IN_FAMILY.getText());
        }
        return member;
    }

    private void validateNotFamilyBossForLeaving(Member member) {
        Family family = member.getFamily();
        if (family != null && family.getMember().getMemberId().equals(member.getMemberId())) {
            throw new IllegalArgumentException(FamilyExceptionMessage.MEMBER_NOT_LEAVE_OWNER.getText());
        }
    }

    private void validateMemberWithoutDog(Member member) {
        if (memberDogRepository.existsByMember(member)) {
            throw new IllegalArgumentException(FamilyExceptionMessage.MEMBER_DOG_FOUND.getText());
        }
    }

    private void validateFamilyNotEmpty(Family family) {
        List<Member> familyMembers = memberRepository.findAllByFamilyId(family.getFamilyId());
        if (familyMembers.size() > 1) {
            throw new IllegalArgumentException(FamilyExceptionMessage.FAMILY_NOT_EMPTY.getText());
        }
    }

    private Dog findDogForMember(Member member) {
        Long dogId = findMemberDogByIdOrThrowException(member.getMemberId()).getDog().getDogId();
        return findDogByIdOrThrowException(dogId);
    }

    private Family getFamilyByInviteCode(String inviteCode) {
        String familyIdStr = redisTemplate.opsForValue().get(REDIS_INVITE_KEY_PREFIX + inviteCode);
        if (familyIdStr == null) {
            throw new IllegalArgumentException(FamilyExceptionMessage.INVALID_INVITE_CODE.getText());
        }
        Long familyId = Long.valueOf(familyIdStr);
        return findFamilyByIdOrThrowException(familyId);
    }

    private void addMemberToFamilyAssociations(Member member, Family family) {
        member.updateFamily(family);
        List<Dog> dogs = dogRepository.findAllByFamilyId(family.getFamilyId());
        dogs.forEach(dog -> memberDogRepository.save(
                MemberDog.builder()
                        .member(member)
                        .dog(dog)
                        .build()
        ));
    }

    @Override
    @Transactional
    public void deleteFamilyAndMembersAndDogs(Family family, Member currentMember) {
        // 가족에 속한 멤버들 방출
        List<Member> familyMembers = memberRepository.findAllByFamilyId(family.getFamilyId());
        for (Member familyMember : familyMembers) {
                walkScheduleRepository.deleteByMemberId(familyMember.getMemberId());
                memberDogRepository.softDeleteByMember(familyMember);
                familyMember.updateFamily(null);
            }

        // 패밀리에 속한 강아지들 삭제
        List<Dog> dogs = dogRepository.findAllByFamilyId(family.getFamilyId());
        for (Dog dog : dogs) {
            dogRepository.softDeleteById(dog.getDogId());
        }

        // 패밀리 삭제
        familyRepository.softDeleteFamilyById(family.getFamilyId());
    }

    private String generateInviteCode(Long familyId) {
        String code;
        boolean isSet;
        do {
            code = UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();
            isSet = Boolean.TRUE.equals(redisTemplate.opsForValue().setIfAbsent(REDIS_INVITE_KEY_PREFIX + code, String.valueOf(familyId), Duration.ofMinutes(5)));
        } while (!isSet);
        return code;
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
