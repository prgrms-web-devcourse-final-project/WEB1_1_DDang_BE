package team9.ddang.family.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import team9.ddang.family.controller.request.WalkScheduleCreateRequest;
import team9.ddang.family.controller.request.WalkScheduleDeleteRequest;
import team9.ddang.family.service.WalkScheduleService;
import team9.ddang.family.service.response.WalkScheduleResponse;
import team9.ddang.member.oauth2.CustomOAuth2User;
import team9.ddang.global.api.ApiResponse;

import java.util.List;

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
                    요청 본문에는 산책 시간(walkTime), 요일 리스트(dayOfWeek)가 포함되어야 합니다.
                    """,
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "산책 일정 생성 요청 데이터",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = WalkScheduleCreateRequest.class)
                    )
            )
    )
    public ApiResponse<List<WalkScheduleResponse>> createWalkSchedule(
            @Valid @RequestBody WalkScheduleCreateRequest request,
            @AuthenticationPrincipal CustomOAuth2User currentUser
    ) {
        List<WalkScheduleResponse> response = walkScheduleService.createWalkSchedule(request.toServiceRequest(), currentUser.getMember());
        return ApiResponse.created(response);
    }


    @GetMapping
    @Operation(
            summary = "산책 일정 리스트 조회",
            description = """
                현재 로그인된 사용자가 소속된 Family ID에 해당하는 모든 산책 일정을 조회합니다.
                """
    )
    public ApiResponse<List<WalkScheduleResponse>> getWalkSchedules(
            @AuthenticationPrincipal CustomOAuth2User currentUser
    ) {
        List<WalkScheduleResponse> response = walkScheduleService.getWalkSchedulesByFamilyId(currentUser.getMember());
        return ApiResponse.ok(response);
    }

    @GetMapping("/{memberId}")
    @Operation(
            summary = "특정 맴버 산책 일정 리스트 조회",
            description = """
                지정한 사용자의 모든 산책 일정을 조회합니다.
                """
    )
    public ApiResponse<List<WalkScheduleResponse>> getMemberWalkSchedules(
            @PathVariable Long memberId,
            @AuthenticationPrincipal CustomOAuth2User currentUser
    ) {
        List<WalkScheduleResponse> response = walkScheduleService.getWalkSchedulesByMemberId(memberId, currentUser.getMember());
        return ApiResponse.ok(response);
    }



    @DeleteMapping
    @Operation(
            summary = "산책 일정 리스트 삭제",
            description = """
                산책 일정 ID를 기준으로 산책 일정을 삭제합니다.
                요청 사용자가 해당 가족에 속하지 않은 경우 또는 권한이 없는 경우 삭제가 불가능합니다.
                성공시 204 No Content를 반환합니다.
                """
    )
    public ApiResponse<Void> deleteWalkSchedule(
            @Valid @RequestBody WalkScheduleDeleteRequest request,
            @AuthenticationPrincipal CustomOAuth2User currentUser
    ) {
        walkScheduleService.deleteWalkSchedule(request.toServiceRequest(), currentUser.getMember());
        return ApiResponse.noContent();
    }

}
