package team9.ddang.walk.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import team9.ddang.global.api.ApiResponse;
import team9.ddang.member.oauth2.CustomOAuth2User;
import team9.ddang.walk.controller.request.log.GetLogByDateRequest;
import team9.ddang.walk.service.WalkLogService;
import team9.ddang.walk.service.response.log.WalkLogByFamilyResponse;
import team9.ddang.walk.service.response.log.WalkLogResponse;
import team9.ddang.walk.service.response.log.WalkStaticsResponse;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/log")
@Tag(name = "DDang DDang log API", description = "댕댕로그 API")
public class WalkLogController {

    private final WalkLogService walkLogService;

    @Operation(
            summary = "산책한 날짜 리스트 조회",
            description = """
                    강아지가 각각 산책을 완료한 날짜의 리스트를 반환합니다.
                    """,
            parameters = {
                    @Parameter(
                            name = "Authorization",
                            description = "액세스 토큰",
                            required = true,
                            in = ParameterIn.HEADER,
                            schema = @Schema(type = "string", example = "Bearer eyJhbGciOiJIUzI1...")
                    )
            }
    )
    @GetMapping("")
    public ApiResponse<List<LocalDate>> getWalkLogs(@AuthenticationPrincipal CustomOAuth2User customOAuth2User){
        List<LocalDate> response = walkLogService.getWalkLogs(customOAuth2User.getMember());
        return ApiResponse.ok(response);
    }

    @Operation(
            summary = "산책 내역 상세 조회",
            description = """
                    산책을 한 날짜의 상세 산책 내역을 조회합니다. 
                    """,
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "조회하고자 하는 날짜의 값",
            required = true,
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = GetLogByDateRequest.class)
            )
    ),
            parameters = {
                    @Parameter(
                            name = "Authorization",
                            description = "액세스 토큰",
                            required = true,
                            in = ParameterIn.HEADER,
                            schema = @Schema(type = "string", example = "Bearer eyJhbGciOiJIUzI1...")
                    )
            }
    )
    @GetMapping("/date")
    public ApiResponse<List<WalkLogResponse>> getWalkLogByDate(@AuthenticationPrincipal CustomOAuth2User customOAuth2User,
                                                         @RequestBody @Valid GetLogByDateRequest getLogByDateRequest){
        List<WalkLogResponse> response = walkLogService.getWalkLogByDate(customOAuth2User.getMember(), getLogByDateRequest.toService());
        return ApiResponse.ok(response);
    }

    @Operation(
            summary = "올해 월별 산책 기록 조회",
            description = """
                    올해 월별로 나누어 산책 횟수를 조회합니다.
                    총 12개의 사이즈를 가진 리스트가 반환되며 0번인 달은
                    0으로 값이 들어가 있습니다.
                    """,
            parameters = {
                    @Parameter(
                            name = "Authorization",
                            description = "액세스 토큰",
                            required = true,
                            in = ParameterIn.HEADER,
                            schema = @Schema(type = "string", example = "Bearer eyJhbGciOiJIUzI1...")
                    )
            }
    )
    @GetMapping("/year")
    public ApiResponse<List<Integer>> getYearlyWalkLog(@AuthenticationPrincipal CustomOAuth2User customOAuth2User){
        List<Integer> response = walkLogService.getYearlyWalkLog(customOAuth2User.getMember());
        return ApiResponse.ok(response);
    }

    @Operation(
            summary = "올해 가족 별 산책 기록",
            description = """
                    올해 가족 별로 나누어 산책 기록을 조회합니다. 로그인한 멤버는 가장 앞
                    리스트에 위치하고 나머지는 횟수별로 높은 순으로 순차적으로 나옵니다.
                    """,
            parameters = {
                    @Parameter(
                            name = "Authorization",
                            description = "액세스 토큰",
                            required = true,
                            in = ParameterIn.HEADER,
                            schema = @Schema(type = "string", example = "Bearer eyJhbGciOiJIUzI1...")
                    )
            }
    )
    @GetMapping("/year/family")
    public ApiResponse<List<WalkLogByFamilyResponse>> getYearlyWalkLogByFamily(@AuthenticationPrincipal CustomOAuth2User customOAuth2User){
        List<WalkLogByFamilyResponse> response = walkLogService.getYearlyWalkLogByFamily(customOAuth2User.getMember());
        return ApiResponse.ok(response);
    }

    @Operation(
            summary = "총 산책 기록 조회",
            description = """
                    강아지를 기준으로 전체 산책 기록의 통계를 조회합니다.
                    """,
            parameters = {
                    @Parameter(
                            name = "Authorization",
                            description = "액세스 토큰",
                            required = true,
                            in = ParameterIn.HEADER,
                            schema = @Schema(type = "string", example = "Bearer eyJhbGciOiJIUzI1...")
                    )
            }
    )
    @GetMapping("/total")
    public ApiResponse<WalkStaticsResponse> getTotalWalkLog(@AuthenticationPrincipal CustomOAuth2User customOAuth2User){
        WalkStaticsResponse response = walkLogService.getTotalWalkLog(customOAuth2User.getMember());
        return ApiResponse.ok(response);
    }

    @Operation(
            summary = "이번달 산책 기록 조회",
            description = """
                    강아지를 기준으로 이번달 산책 기록의 통계를 조회합니다.
                    """,
            parameters = {
                    @Parameter(
                            name = "Authorization",
                            description = "액세스 토큰",
                            required = true,
                            in = ParameterIn.HEADER,
                            schema = @Schema(type = "string", example = "Bearer eyJhbGciOiJIUzI1...")
                    )
            }
    )
    @GetMapping("/total/month")
    public ApiResponse<WalkStaticsResponse> getMonthlyTotalWalk(@AuthenticationPrincipal CustomOAuth2User customOAuth2User){
        WalkStaticsResponse response = walkLogService.getMonthlyTotalWalk(customOAuth2User.getMember());
        return ApiResponse.ok(response);
    }

}
