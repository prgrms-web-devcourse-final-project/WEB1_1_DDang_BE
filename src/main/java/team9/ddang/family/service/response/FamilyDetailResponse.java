package team9.ddang.family.service.response;

import team9.ddang.dog.service.response.GetDogResponse;
import team9.ddang.family.entity.Family;
import team9.ddang.member.entity.Member;
import team9.ddang.member.service.response.MemberResponse;

import java.util.List;

public record FamilyDetailResponse(
        Long familyId,
        Long memberId,
        String familyName,
        List<MemberResponse> members,
        List<GetDogResponse> dogs
) {
    public FamilyDetailResponse(Family family, List<MemberResponse> members, List<GetDogResponse> dogs) {
        this(
                family.getFamilyId(),
                family.getMember().getMemberId(),
                family.getFamilyName(),
                members,
                dogs
        );
    }
}
