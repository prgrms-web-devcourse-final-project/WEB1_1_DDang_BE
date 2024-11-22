package team9.ddang.family.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team9.ddang.family.controller.request.FamilyCreateRequest;
import team9.ddang.family.entity.Family;
import team9.ddang.family.exception.FamilyExceptionMessage;
import team9.ddang.family.repository.FamilyRepository;
import team9.ddang.family.service.response.FamilyResponse;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class FamilyServiceImpl implements FamilyService {

    private final FamilyRepository familyRepository;

    @Override
    @Transactional
    public FamilyResponse createFamily(FamilyCreateRequest request) {
        String familyCode = generateUniqueFamilyCode();

        Family family = Family.builder()
                .familyName(request.familyName())
                .familyCode(familyCode)
                .build();

        family = familyRepository.save(family);

        return new FamilyResponse(family);
    }

    @Override
    @Transactional(readOnly = true)
    public String getFamilyCode(Long familyId) {
        Family family = findFamilyByIdOrThrowException(familyId);
        return family.getFamilyCode();
    }

    @Override
    @Transactional
    public void deleteFamily(Long familyId) {
        // TODO 생각할 점이 많다.
    }

    private String generateUniqueFamilyCode() {
        String code;
        do {
            code = UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();
        } while (familyRepository.existsByFamilyCode(code));
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
