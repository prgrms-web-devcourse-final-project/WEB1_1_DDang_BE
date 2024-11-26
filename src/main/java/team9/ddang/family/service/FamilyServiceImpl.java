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
import team9.ddang.family.controller.request.FamilyCreateRequest;
import team9.ddang.family.entity.Family;
import team9.ddang.family.exception.FamilyExceptionMessage;
import team9.ddang.family.repository.FamilyRepository;
import team9.ddang.family.service.response.FamilyResponse;
import team9.ddang.family.service.response.InviteCodeResponse;
import team9.ddang.member.entity.Member;
import team9.ddang.member.repository.MemberRepository;

import java.time.Duration;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class FamilyServiceImpl implements FamilyService {

    private final RedisTemplate<String, String> redisTemplate;
    private final FamilyRepository familyRepository;
    private final MemberRepository memberRepository;
    private final DogRepository dogRepository;
    private final MemberDogRepository memberDogRepository;

    @Override
    @Transactional
    public FamilyResponse createFamily(FamilyCreateRequest request, Member member) {

        Member currentMember = findMemberByIdOrThrowException(member.getMemberId());

        if(currentMember.getFamily() != null) {
            throw new IllegalArgumentException(FamilyExceptionMessage.MEMBER_ALREADY_IN_FAMILY.getText());
        }

        // TODO : 나중에 여러 강아지를 키울 수 있게 된다면 강아지를 리스트로 받아와야 할 듯
        Long dogId = findMemberDogByIdOrThrowException(currentMember.getMemberId()).getDog().getDogId();
        Dog dog = findDogByIdOrThrowException(dogId);


        Family family = Family.builder()
                .member(currentMember)
                .familyName(request.familyName())
                .build();

        family = familyRepository.save(family);

        currentMember.updateFamily(family);
        dog.updateFamily(family);

        return new FamilyResponse(family);
    }

    @Override
    public InviteCodeResponse createInviteCode(Member member){

        Member currentMember = findMemberByIdOrThrowException(member.getMemberId());

        if(currentMember.getFamily() == null) {
            throw new IllegalArgumentException(FamilyExceptionMessage.MEMBER_NOT_IN_FAMILY.getText());
        }

        Family family = currentMember.getFamily();

        String redisKey = "invite:" + family.getFamilyId();

        Long ttl = redisTemplate.getExpire(redisKey);
        if (ttl != null && ttl > 0) {
            String existingInviteCode = redisTemplate.opsForValue().get(redisKey);
            return new InviteCodeResponse(existingInviteCode, ttl);
        }

        String newInviteCode = generateInviteCode();
        redisTemplate.opsForValue().set(redisKey, newInviteCode, Duration.ofMinutes(5));

        return new InviteCodeResponse(newInviteCode, Duration.ofMinutes(5).toSeconds());

    }


    @Override
    @Transactional
    public FamilyResponse addMemberToFamily(String inviteCode, Member member){
        Member currentMember = findMemberByIdOrThrowException(member.getMemberId());

        String familyIdStr = redisTemplate.opsForValue().get("invite:" + inviteCode);
        if (familyIdStr == null) {
            throw new IllegalArgumentException(FamilyExceptionMessage.INVALID_INVITE_CODE.getText());
        }

        if(currentMember.getFamily() != null) {
            throw new IllegalArgumentException(FamilyExceptionMessage.MEMBER_ALREADY_IN_FAMILY.getText());
        }

        boolean hasDog = memberDogRepository.existsByMember(member);
        if (hasDog) {
            throw new IllegalArgumentException(FamilyExceptionMessage.MEMBER_DOG_FOUND.getText());
        }

        Long familyId = Long.valueOf(familyIdStr);

        Family family = findFamilyByIdOrThrowException(familyId);


        member.updateFamily(family);

        return new FamilyResponse(family);
    }




    @Override
    @Transactional
    public void deleteFamily(Long familyId) {
        // TODO 생각할 점이 많다.
    }

    private String generateInviteCode() {
        String code;
        boolean isSet;
        do {
            code = UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();
            isSet = Boolean.TRUE.equals(redisTemplate.opsForValue().setIfAbsent("invite:" + code, "TEMP", Duration.ofSeconds(10)));
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
