package team9.ddang.notification.service.response;

import io.swagger.v3.oas.annotations.media.Schema;
import team9.ddang.notification.entity.IsAgreed;
import team9.ddang.notification.entity.NotificationSettings;
import team9.ddang.notification.entity.Type;

@Schema(description = "알림 설정 업데이트 응답 데이터")
public record SettingsUpdateResponse(
        @Schema(description = "알림 설정 ID", example = "1")
        Long notificationSettingsId,

        @Schema(description = "회원 ID", example = "1")
        Long memberId,

        @Schema(description = "알림 타입", example = "FAMILY")
        Type type,

        @Schema(description = "알림 설정 여부", example = "true")
        IsAgreed isAgreed
) {
    public static SettingsUpdateResponse from(NotificationSettings notificationSettings) {
        return new SettingsUpdateResponse(
                notificationSettings.getNotificationSettingId(),
                notificationSettings.getMember().getMemberId(),
                notificationSettings.getType(),
                notificationSettings.getIsAgreed()
        );
    }
}
