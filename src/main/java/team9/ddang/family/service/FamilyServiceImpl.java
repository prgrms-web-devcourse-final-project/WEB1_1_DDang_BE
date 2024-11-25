package team9.ddang.family.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team9.ddang.family.controller.request.FamilyCreateRequest;
import team9.ddang.family.entity.Family;
import team9.ddang.family.exception.FamilyExceptionMessage;
import team9.ddang.family.repository.FamilyRepository;
import team9.ddang.family.service.response.FamilyResponse;
import team9.ddang.family.service.response.InviteCodeResponse;

import java.time.Duration;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class FamilyServiceImpl implements FamilyService {

    private final RedisTemplate<String, String> redisTemplate;
    private final FamilyRepository familyRepository;

    @Override
    @Transactional
    public FamilyResponse createFamily(FamilyCreateRequest request) {

        // TODO : 맴버가 이미 가족이 있는지 확인
        // TODO : 맴버의 familyID를 업데이트
        // TODO : 맴버가 소유한 강아지들의  familyID도 업데이트
        // TODO : 생성 요청한 맴버가 패밀리댕의 주인

        Family family = Family.builder()
                .familyName(request.familyName())
                .build();

        family = familyRepository.save(family);

        return new FamilyResponse(family);
    }

    @Override
    public InviteCodeResponse createInviteCode(Long familyId){

        Family family = findFamilyByIdOrThrowException(familyId);
        // TODO 맴버가 해당 패밀리에 속해있는지 검증

        String redisKey = "invite:" + familyId;

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
}
