package team9.ddang.member.controller;

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
    public ApiResponse<Void> createReport(
            @AuthenticationPrincipal CustomOAuth2User customOAuth2User,
            @RequestBody ReportRequest reportRequest
    ) {
        reportService.createReport(customOAuth2User.getMember(), reportRequest.toServiceRequest());
        return ApiResponse.noContent();
    }
}
