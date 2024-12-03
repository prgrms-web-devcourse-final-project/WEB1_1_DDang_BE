package team9.ddang.notification.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import team9.ddang.global.api.ApiResponse;
import team9.ddang.member.oauth2.CustomOAuth2User;
import team9.ddang.notification.service.NotificationService;
import team9.ddang.notification.service.response.NotificationResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/notification")
@Tag(name = "Notification API", description = "알림 API")
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping("/list")
    @Operation(
            summary = "알림 목록 조회",
            description = "로그인한 사용자의 알림 목록을 최신순으로 10개씩 페이징하여 반환합니다.",
            parameters = {
                    @Parameter(name = "page", description = "조회할 페이지 번호 (기본값: 0)", required = false)
            }
    )
    public ApiResponse<Slice<NotificationResponse>> getNotificationList(
            @AuthenticationPrincipal CustomOAuth2User customOAuth2User,
            @RequestParam(defaultValue = "0") int page // 기본값은 첫 페이지
    ) {
        if (page < 0) {
            throw new IllegalArgumentException("페이지 번호는 0 이상이어야 합니다.");
        }
        PageRequest pageRequest = PageRequest.of(page, 10, Sort.by("createdAt").descending());  // 최신순 정렬
        Slice<NotificationResponse> notificationList = notificationService.getNotificationList(customOAuth2User.getMember(), pageRequest);
        return ApiResponse.ok(notificationList);
    }
}
