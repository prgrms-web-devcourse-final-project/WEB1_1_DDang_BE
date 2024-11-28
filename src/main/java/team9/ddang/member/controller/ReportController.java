package team9.ddang.member.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import team9.ddang.global.api.ApiResponse;
import team9.ddang.global.api.WebSocketResponse;
import team9.ddang.member.controller.request.ReportRequest;
import team9.ddang.member.entity.Member;
import team9.ddang.member.oauth2.CustomOAuth2User;
import team9.ddang.member.service.ReportService;

@RestController
@RequestMapping("/api/v1/reports")
@RequiredArgsConstructor
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
