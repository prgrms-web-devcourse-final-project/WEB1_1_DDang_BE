package team9.ddang.notification.service.response;

import io.swagger.v3.oas.annotations.media.Schema;
import team9.ddang.member.entity.IsMatched;
import team9.ddang.notification.entity.IsAgreed;
import team9.ddang.notification.entity.NotificationSettings;
import team9.ddang.notification.entity.Type;

import java.util.LinkedHashMap;
import java.util.Map;

@Schema(description = "알림 설정 응답 데이터")
public record SettingsResponse(

        @Schema(description = "회원 ID", example = "1")
        Long memberId,

        @Schema(description = "강번따 허용 여부", example = "TRUE")
        IsMatched isMatched,

        @Schema(description = "알림 설정 데이터")
        Map<String, NotificationDetail> settings
) {
    public static SettingsResponse from(Long memberId, IsMatched isMatched, NotificationSettings walkSettings, NotificationSettings chatSettings) {
        Map<String, NotificationDetail> orderedSettings = new LinkedHashMap<>();
        orderedSettings.put("WALK", NotificationDetail.from(walkSettings));
        orderedSettings.put("CHAT", NotificationDetail.from(chatSettings));

        return new SettingsResponse(memberId, isMatched, orderedSettings);
    }

    public record NotificationDetail(
            @Schema(description = "알림 설정 ID", example = "1")
            Long notificationSettingId,

            @Schema(description = "알림 타입", example = "WALK")
            Type type,

            @Schema(description = "알림 설정 여부", example = "TRUE")
            IsAgreed isAgreed
    ) {
        public static NotificationDetail from(NotificationSettings settings) {
            return new NotificationDetail(
                    settings.getNotificationSettingId(),
                    settings.getType(),
                    settings.getIsAgreed()
            );
        }
    }
}