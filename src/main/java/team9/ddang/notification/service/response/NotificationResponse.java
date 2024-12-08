package team9.ddang.notification.service.response;

import io.swagger.v3.oas.annotations.media.Schema;
import team9.ddang.notification.entity.IsRead;
import team9.ddang.notification.entity.Notification;
import team9.ddang.notification.entity.Type;

import java.time.LocalDateTime;

@Schema(description = "알림 응답 데이터")
public record NotificationResponse(
        @Schema(description = "알림 ID", example = "1")
        Long notificationId,

        @Schema(description = "알림 타입", example = "WALK")
        Type type,

        @Schema(description = "알림 내용", example = "아빠! 산책가요.")
        String content,

        @Schema(description = "알림 읽음 여부", example = "FALSE")
        IsRead isRead,

        @Schema(description = "회원 ID", example = "1")
        Long memberId,

        @Schema(description = "알림 생성일시", example = "2021-07-01T00:00:00")
        LocalDateTime createdAt
) {
    public static NotificationResponse of(Notification notification) {
        return new NotificationResponse(
                notification.getNotificationId(),
                notification.getType(),
                notification.getContent(),
                notification.getIsRead(),
                notification.getMember().getMemberId(),
                notification.getCreatedAt()
        );
    }
}
