package team9.ddang.walk.service.response.log;

import team9.ddang.member.entity.FamilyRole;

public record WalkLogByFamilyResponse(
        Long memberId,
        FamilyRole familyRole,
        int count
) {
}
