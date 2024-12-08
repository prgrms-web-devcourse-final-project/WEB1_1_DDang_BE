package team9.ddang.member.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import team9.ddang.global.api.ApiResponse;
import team9.ddang.member.controller.request.ReportRequest;
import team9.ddang.member.oauth2.CustomOAuth2User;
import team9.ddang.member.service.ReportService;

@RestController
@RequestMapping("/api/v1/reports")
@RequiredArgsConstructor
@Tag(name = "Reports", description = "신고 관련 API")
public class ReportController {
    private final ReportService reportService;

    @PostMapping
    @Operation(
            summary = "신고 생성",
            description = """
                    특정 회원에 대한 신고를 생성합니다.
                    본인은 신고할 수 없습니다.
                    """
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "204",
                    description = "채팅방 생성 성공 (이미 존재하는 경우에도 동일)",
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
                                            value = "{ \"code\": 400, \"status\": \"BAD_REQUEST\", \"message\": \"해당 유저를 찾을 수 없습니다.\", \"data\": null }"
                                    ),
                                    @ExampleObject(
                                            name = "유효하지 않은 요청 데이터",
                                            value = "{ \"code\": 400, \"status\": \"BAD_REQUEST\", \"message\": \"신고 대상 member의 ID는 필수입니다.\", \"data\": null }"
                                    ),
                                    @ExampleObject(
                                            name = "본인을 신고하려고 하는 경우",
                                            value = "{ \"code\": 400, \"status\": \"BAD_REQUEST\", \"message\": \"본인을 신고할 수 없습니다.\", \"data\": null }"
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
    public ApiResponse<Void> createReport(
            @AuthenticationPrincipal CustomOAuth2User customOAuth2User,
            @RequestBody ReportRequest reportRequest
    ) {
        reportService.createReport(customOAuth2User.getMember(), reportRequest.toServiceRequest());
        return ApiResponse.noContent();
    }
}
