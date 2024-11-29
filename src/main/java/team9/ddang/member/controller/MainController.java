package team9.ddang.member.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import team9.ddang.global.api.ApiResponse;
import team9.ddang.member.controller.request.AddFriendRequest;
import team9.ddang.member.oauth2.CustomOAuth2User;
import team9.ddang.member.service.MainService;
import team9.ddang.member.service.response.MainResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/main")
@Tag(name = "Main API", description = "메인 화면 API")
public class MainController {

    private final MainService mainService;

    @Operation(
            summary = "메인 화면 조회",
            description = "메인에 띄울 정보를 조회 합니다. 만약 오늘 산책 멤버가 없을시 familyRole 이 null 로 표현 됩니다.",
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
    public ApiResponse<MainResponse> getMain(@AuthenticationPrincipal CustomOAuth2User customOAuth2User){
        MainResponse response = mainService.getMain(customOAuth2User.getMember());
        return ApiResponse.ok(response);
    }
}
