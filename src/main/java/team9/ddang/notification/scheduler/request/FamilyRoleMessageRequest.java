package team9.ddang.notification.scheduler.request;

import team9.ddang.member.entity.FamilyRole;

public record FamilyRoleMessageRequest(FamilyRole familyRole, String message) {

    public FamilyRoleMessageRequest(FamilyRole familyRole) {
        this(familyRole, createFamilyRoleMessage(familyRole));
    }

    private static String createFamilyRoleMessage(FamilyRole familyRole) {
        switch (familyRole) {
            case FATHER:
                return "아빠!";
            case MOTHER:
                return "엄마!";
            case SISTER:
                return "누나!";
            case BROTHER:
                return "형!";
            default:
                return "";
        }
    }

    public static FamilyRoleMessageRequest from(FamilyRole familyRole) {
        return new FamilyRoleMessageRequest(familyRole);
    }
}
