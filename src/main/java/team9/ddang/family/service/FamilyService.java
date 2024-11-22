package team9.ddang.family.service;

import team9.ddang.family.controller.request.FamilyCreateRequest;
import team9.ddang.family.service.response.FamilyResponse;

public interface FamilyService {
    FamilyResponse createFamily(FamilyCreateRequest request);

    String getFamilyCode(Long familyId);

    void deleteFamily(Long familyId);
}
