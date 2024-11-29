package team9.ddang.member.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import team9.ddang.global.api.ApiResponse;
import team9.ddang.member.oauth2.CustomOAuth2User;
import team9.ddang.member.service.MainService;
import team9.ddang.member.service.response.MainResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/main")
@Tag(name = "Main API", description = "메인 화면 API")
public class MainController {

    private final MainService mainService;

    @GetMapping("")
    public ApiResponse<MainResponse> getMain(@AuthenticationPrincipal CustomOAuth2User customOAuth2User){
        MainResponse response = mainService.getMain(customOAuth2User.getMember());
        return ApiResponse.ok(response);
    }
}
