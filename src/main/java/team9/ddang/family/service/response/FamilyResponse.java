package team9.ddang.family.service.response;

import team9.ddang.family.entity.Family;

public record FamilyResponse(
        Long familyId,
        Long memberId,
        String familyName
) {
    public FamilyResponse(Family family) {
        this(
                family.getFamilyId(),
                family.getMember().getMemberId(),
                family.getFamilyName()
        );
    }

}
