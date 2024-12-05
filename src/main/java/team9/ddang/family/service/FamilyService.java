package team9.ddang.family.service;

import team9.ddang.family.entity.Family;
import team9.ddang.family.service.response.FamilyDetailResponse;
import team9.ddang.family.service.response.FamilyResponse;
import team9.ddang.family.service.response.InviteCodeResponse;
import team9.ddang.member.entity.Member;

public interface FamilyService {
    FamilyResponse createFamily(Member member);

    InviteCodeResponse createInviteCode(Member member);

    FamilyResponse addMemberToFamily(String inviteCode, Member member);

    FamilyDetailResponse getMyFamily(Member member);

    void removeMemberFromFamily(Long memberIdToRemove, Member member);

    void deleteFamilyAndMembersAndDogs(Family family, Member currentMember);

    void leaveFamily(Member member);

    void deleteFamily(Member member);
}
