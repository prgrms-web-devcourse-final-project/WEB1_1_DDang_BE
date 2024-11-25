package team9.ddang.family.service;

import team9.ddang.family.controller.request.FamilyCreateRequest;
import team9.ddang.family.service.response.FamilyResponse;
import team9.ddang.family.service.response.InviteCodeResponse;

public interface FamilyService {
    FamilyResponse createFamily(FamilyCreateRequest request);

    InviteCodeResponse createInviteCode(Long familyId);

    void deleteFamily(Long familyId);
}
