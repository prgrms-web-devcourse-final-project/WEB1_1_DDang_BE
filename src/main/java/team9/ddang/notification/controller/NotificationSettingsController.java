package team9.ddang.notification.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import team9.ddang.global.api.ApiResponse;
import team9.ddang.member.oauth2.CustomOAuth2User;
import team9.ddang.notification.controller.request.NotificationSettingsRequest;
import team9.ddang.notification.service.NotificationSettingsService;
import team9.ddang.notification.service.response.SettingsResponse;
import team9.ddang.notification.service.response.SettingsUpdateResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/notification-settings")
@Tag(name = "Notification Settings API", description = "알림 설정 API")
public class NotificationSettingsController {

    private final NotificationSettingsService notificationSettingsService;

    @GetMapping
    @Operation(summary = "알림 설정 조회", description = "알림 설정 조회 API")
    public ApiResponse<SettingsResponse> getSettings(@AuthenticationPrincipal CustomOAuth2User customOAuth2User) {
        return ApiResponse.ok(notificationSettingsService.getSettings(customOAuth2User.getMember().getMemberId()));
    }

    @PatchMapping("/update")
    @Operation(
            summary = "알림 설정 수정",
            description = "알림 설정 수정 API",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "알림 설정 정보",
                    required = true,
                    content = @io.swagger.v3.oas.annotations.media.Content(
                            mediaType = "application/json",
                            schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = NotificationSettingsRequest.class)
                    )
            )
    )
    public ApiResponse<SettingsUpdateResponse> updateSettings(@AuthenticationPrincipal CustomOAuth2User customOAuth2User,
                                                              @RequestBody @Valid NotificationSettingsRequest notificationSettingsRequest) {

        return ApiResponse.ok(notificationSettingsService.updateSettings(customOAuth2User.getMember().getMemberId(), notificationSettingsRequest));
    }
}

