package team9.ddang.family.service.response;

import team9.ddang.family.entity.Family;

public record FamilyResponse(
        Long familyId,
        String familyName,
        String familyCode
) {
    public FamilyResponse(Family family) {
        this(
                family.getFamilyId(),
                family.getFamilyName(),
                family.getFamilyCode()
        );
    }

}
