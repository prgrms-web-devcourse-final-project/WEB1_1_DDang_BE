package team9.ddang.family.service;

import team9.ddang.family.controller.request.FamilyCreateRequest;
import team9.ddang.family.service.response.FamilyResponse;
import team9.ddang.family.service.response.InviteCodeResponse;
import team9.ddang.member.entity.Member;

public interface FamilyService {
    FamilyResponse createFamily(FamilyCreateRequest request, Member member);

    InviteCodeResponse createInviteCode(Member member);

    void addMemberToFamily(String inviteCode);

    void deleteFamily(Long familyId);
}
