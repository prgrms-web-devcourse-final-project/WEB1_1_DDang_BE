package team9.ddang.family.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import team9.ddang.family.controller.request.WalkScheduleCreateRequest;
import team9.ddang.family.service.WalkScheduleService;
import team9.ddang.family.service.response.WalkScheduleResponse;
import team9.ddang.member.oauth2.CustomOAuth2User;
import team9.ddang.global.api.ApiResponse;

@RestController
@RequestMapping("/api/v1/walk-schedules")
@RequiredArgsConstructor
@Tag(name = "Walk Schedule API", description = "산책 일정 API")
public class WalkScheduleController {

    private final WalkScheduleService walkScheduleService;

    @PostMapping
    @Operation(
            summary = "산책 일정 생성",
            description = """
                    새로운 산책 일정을 생성합니다.
                    요청 본문에는 산책 시간(walkTime), 요일(dayOfWeek), 강아지 ID(dogId)가 포함되어야 합니다.
                    """,
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "산책 일정 생성 요청 데이터",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = WalkScheduleCreateRequest.class)
                    )
            ),
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "201",
                            description = "산책 일정 생성 성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = WalkScheduleResponse.class)
                            )
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "400",
                            description = "잘못된 요청 데이터",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ApiResponse.class)
                            )
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "500",
                            description = "서버 오류",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ApiResponse.class)
                            )
                    )
            }
    )
    public ApiResponse<WalkScheduleResponse> createWalkSchedule(
            @Valid @RequestBody WalkScheduleCreateRequest request,
            @AuthenticationPrincipal CustomOAuth2User currentUser
    ) {
        WalkScheduleResponse response = walkScheduleService.createWalkSchedule(request.toServiceRequest(), currentUser.getMember());
        return ApiResponse.created(response);
    }

}
