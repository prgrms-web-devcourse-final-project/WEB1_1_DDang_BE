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
import team9.ddang.family.service.response.FamilyDetailResponse;
import team9.ddang.family.service.response.FamilyResponse;
import team9.ddang.family.service.response.InviteCodeResponse;
import team9.ddang.member.entity.Member;
import team9.ddang.member.repository.MemberRepository;
import team9.ddang.member.service.response.MemberResponse;
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

        Member currentMember = findMemberByIdOrThrowException(member.getMemberId());

        if (currentMember.getFamily() != null) {
            throw new IllegalArgumentException(FamilyExceptionMessage.MEMBER_ALREADY_IN_FAMILY.getText());
        }

        // TODO : 나중에 여러 강아지를 키울 수 있게 된다면 강아지를 리스트로 받아와야 할 듯
        Long dogId = findMemberDogByIdOrThrowException(currentMember.getMemberId()).getDog().getDogId();
        Dog dog = findDogByIdOrThrowException(dogId);


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
        Member currentMember = findMemberByIdOrThrowException(member.getMemberId());

        if (currentMember.getFamily() == null) {
            throw new IllegalArgumentException(FamilyExceptionMessage.MEMBER_NOT_IN_FAMILY.getText());
        }

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

        // 새로운 초대 코드 생성
        String newInviteCode = generateInviteCode(family.getFamilyId());
        redisTemplate.opsForValue().set(REDIS_INVITE_KEY_PREFIX + newInviteCode, String.valueOf(family.getFamilyId()), Duration.ofMinutes(5));

        return new InviteCodeResponse(family, newInviteCode, Duration.ofMinutes(5).toSeconds());
    }


    @Override
    @Transactional
    public FamilyResponse addMemberToFamily(String inviteCode, Member member) {
        Member currentMember = findMemberByIdOrThrowException(member.getMemberId());

        String familyIdStr = redisTemplate.opsForValue().get(REDIS_INVITE_KEY_PREFIX + inviteCode);
        if (familyIdStr == null) {
            throw new IllegalArgumentException(FamilyExceptionMessage.INVALID_INVITE_CODE.getText());
        }

        if (currentMember.getFamily() != null) {
            throw new IllegalArgumentException(FamilyExceptionMessage.MEMBER_ALREADY_IN_FAMILY.getText());
        }

        boolean hasDog = memberDogRepository.existsByMember(member);
        if (hasDog) {
            throw new IllegalArgumentException(FamilyExceptionMessage.MEMBER_DOG_FOUND.getText());
        }

        Long familyId = Long.valueOf(familyIdStr);

        Family family = findFamilyByIdOrThrowException(familyId);

        currentMember.updateFamily(family);

        List<Dog> dogs = dogRepository.findAllByFamilyId(family.getFamilyId());

        for (Dog dog : dogs) {
            MemberDog memberDog = MemberDog.builder()
                    .member(currentMember)
                    .dog(dog)
                    .build();
            memberDogRepository.save(memberDog);
        }

        return new FamilyResponse(family);
    }

    @Override
    @Transactional(readOnly = true)
    public FamilyDetailResponse getMyFamily(Member member) {
        Member currentMember = findMemberByIdOrThrowException(member.getMemberId());

        if (currentMember.getFamily() == null) {
            throw new IllegalArgumentException(FamilyExceptionMessage.MEMBER_NOT_IN_FAMILY.getText());
        }

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

        List<MemberResponse> members = memberRepository.findAllByFamilyId(family.getFamilyId())
                .stream()
                .map(MemberResponse::from)
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
        Member currentMember = findMemberByIdOrThrowException(member.getMemberId());

        if (currentMember.getFamily() == null) {
            throw new IllegalArgumentException(FamilyExceptionMessage.MEMBER_NOT_IN_FAMILY.getText());
        }

        Family family = currentMember.getFamily();

        if (!family.getMember().getMemberId().equals(currentMember.getMemberId())) {
            throw new IllegalArgumentException(FamilyExceptionMessage.MEMBER_NOT_FAMILY_BOSS.getText());
        }

        if (family.getMember().getMemberId().equals(memberIdToRemove)) {
            throw new IllegalArgumentException(FamilyExceptionMessage.MEMBER_FAMILY_BOSS.getText());
        }


        Member memberToRemove = findMemberByIdOrThrowException(memberIdToRemove);
        if (memberToRemove.getFamily() == null ||
                !memberToRemove.getFamily().getFamilyId().equals(family.getFamilyId())) {
            throw new IllegalArgumentException(FamilyExceptionMessage.MEMBER_NOT_IN_FAMILY.getText());
        }

        walkScheduleRepository.softDeleteByMemberId(memberToRemove.getMemberId());

        memberDogRepository.softDeleteByMember(memberToRemove);

        memberToRemove.updateFamily(null);
    }

    @Override
    @Transactional
    public void leaveFamily(Member member) {
        Member currentMember = findMemberByIdOrThrowException(member.getMemberId());

        if (currentMember.getFamily() == null) {
            throw new IllegalArgumentException(FamilyExceptionMessage.MEMBER_NOT_IN_FAMILY.getText());
        }

        Family family = currentMember.getFamily();

        if (family.getMember().getMemberId().equals(currentMember.getMemberId())) {
            throw new IllegalArgumentException(FamilyExceptionMessage.MEMBER_NOT_LEAVE_OWNER.getText());
        }

        walkScheduleRepository.softDeleteByMemberId(currentMember.getMemberId());

        memberDogRepository.softDeleteByMember(currentMember);

        currentMember.updateFamily(null);
    }

    @Override
    @Transactional
    public void deleteFamily(Member member) {
        Member currentMember = findMemberByIdOrThrowException(member.getMemberId());

        if (currentMember.getFamily() == null) {
            throw new IllegalArgumentException(FamilyExceptionMessage.MEMBER_NOT_IN_FAMILY.getText());
        }

        Family family = currentMember.getFamily();

        if (!family.getMember().getMemberId().equals(currentMember.getMemberId())) {
            throw new IllegalArgumentException(FamilyExceptionMessage.MEMBER_NOT_FAMILY_BOSS.getText());
        }

        List<Member> familyMembers = memberRepository.findAllByFamilyId(family.getFamilyId());
        if (familyMembers.size() > 1) {
            throw new IllegalArgumentException(FamilyExceptionMessage.FAMILY_NOT_EMPTY.getText());
        }

        walkScheduleRepository.softDeleteByFamilyId(family.getFamilyId());

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
