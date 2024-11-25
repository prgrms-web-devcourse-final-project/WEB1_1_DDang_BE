package team9.ddang.family.service.response;

public record InviteCodeResponse(
        String inviteCode,
        long expiresInSeconds
) {
}
