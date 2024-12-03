package team9.ddang.notification.controller.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import team9.ddang.notification.entity.IsAgreed;
import team9.ddang.notification.entity.Type;

@Schema(description = "알림 설정 요청 데이터")
public record NotificationSettingsRequest(

        @NotNull(message = "알림 타입은 필수입니다.")
        Type type,

        @NotNull(message = "알림 설정 여부는 필수입니다.")
        IsAgreed isAgreed
) {
}
