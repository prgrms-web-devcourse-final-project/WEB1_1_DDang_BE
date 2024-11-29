package team9.ddang.member.controller;

import io.swagger.v3.oas.annotations.Operation;
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
    public ApiResponse<Void> createReport(
            @AuthenticationPrincipal CustomOAuth2User customOAuth2User,
            @RequestBody ReportRequest reportRequest
    ) {
        reportService.createReport(customOAuth2User.getMember(), reportRequest.toServiceRequest());
        return ApiResponse.noContent();
    }
}
