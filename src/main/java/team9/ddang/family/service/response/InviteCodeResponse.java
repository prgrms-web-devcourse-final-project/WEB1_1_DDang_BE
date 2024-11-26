package team9.ddang.family.service.response;

import team9.ddang.family.entity.Family;

public record InviteCodeResponse(
        Long familyId,
        String inviteCode,
        long expiresInSeconds
) {
    public InviteCodeResponse(Family family, String inviteCode, long expiresInSeconds) {
        this(
                family.getFamilyId(),
                inviteCode,
                expiresInSeconds
        );
    }
}
