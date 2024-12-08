package team9.ddang.notification.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "알림 설정 조회 성공",
                    useReturnTypeSchema = true
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "요청 데이터가 유효하지 않은 경우",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = {
                                    @ExampleObject(
                                            name = "존재하지 않는 회원",
                                            value = "{\"code\": 400, \"status\": \"BAD_REQUEST\", \"message\": \"존재하지 않는 회원입니다.\", \"data\": null}"
                                    ),
                                    @ExampleObject(
                                            name = "알림 설정이 존재하지 않은 경우",
                                            value = "{\"code\": 400, \"status\": \"BAD_REQUEST\", \"message\": \"존재하지 않는 알림 설정입니다.\", \"data\": null}"
                                    )
                            }
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "인증 실패 또는 유효하지 않은 토큰으로 접근하려는 경우",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(
                                    name = "인증 실패 예시",
                                    value = "{ \"code\": 401, \"status\": \"UNAUTHORIZED\", \"message\": \"AccessToken is invalid\", \"data\": null }"
                            )
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "500",
                    description = "서버 내부에서 처리되지 않은 오류가 발생한 경우",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(
                                    name = "서버 오류 예시",
                                    value = "{ \"code\": 500, \"status\": \"INTERNAL_SERVER_ERROR\", \"message\": \"알 수 없는 오류가 발생했습니다.\", \"data\": null }"
                            )
                    )
            )
    })
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
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "알림 설정 수정 성공",
                    useReturnTypeSchema = true
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "요청 데이터가 유효하지 않은 경우",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples =
                                    @ExampleObject(
                                            name = "알림 설정이 존재하지 않은 경우",
                                            value = "{\"code\": 400, \"status\": \"BAD_REQUEST\", \"message\": \"해당 유저의 알림 설정이 존재하지 않습니다.\", \"data\": null}"
                                    )
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "인증 실패 또는 유효하지 않은 토큰으로 접근하려는 경우",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(
                                    name = "인증 실패 예시",
                                    value = "{ \"code\": 401, \"status\": \"UNAUTHORIZED\", \"message\": \"AccessToken is invalid\", \"data\": null }"
                            )
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "500",
                    description = "서버 내부에서 처리되지 않은 오류가 발생한 경우",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(
                                    name = "서버 오류 예시",
                                    value = "{ \"code\": 500, \"status\": \"INTERNAL_SERVER_ERROR\", \"message\": \"알 수 없는 오류가 발생했습니다.\", \"data\": null }"
                            )
                    )
            )
    })
    public ApiResponse<SettingsUpdateResponse> updateSettings(@AuthenticationPrincipal CustomOAuth2User customOAuth2User,
                                                              @RequestBody @Valid NotificationSettingsRequest notificationSettingsRequest) {

        return ApiResponse.ok(notificationSettingsService.updateSettings(customOAuth2User.getMember().getMemberId(), notificationSettingsRequest));
    }
}

