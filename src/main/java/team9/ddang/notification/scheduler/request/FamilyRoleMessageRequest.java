package team9.ddang.notification.scheduler.request;

import team9.ddang.member.entity.FamilyRole;

public record FamilyRoleMessageRequest(FamilyRole familyRole, String message) {

    public FamilyRoleMessageRequest(FamilyRole familyRole) {
        this(familyRole, createFamilyRoleMessage(familyRole));
    }

    private static String createFamilyRoleMessage(FamilyRole familyRole) {
        return switch (familyRole) {
            case FATHER -> "아빠!";
            case MOTHER -> "엄마!";
            case ELDER_BROTHER -> "형!";
            case OLDER_BROTHER -> "오빠!";
            case ELDER_SISTER -> "누나!";
            case OLDER_SISTER -> "언니!";
            case GRANDFATHER -> "할아버지!";
            case GRANDMOTHER -> "할머니!";
            default -> "";
        };
    }

    public static FamilyRoleMessageRequest from(FamilyRole familyRole) {
        return new FamilyRoleMessageRequest(familyRole);
    }
}
