package team9.ddang.walk.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import team9.ddang.global.api.ApiResponse;
import team9.ddang.member.oauth2.CustomOAuth2User;
import team9.ddang.walk.controller.request.walk.CompleteWalkRequest;
import team9.ddang.walk.service.WalkService;
import team9.ddang.walk.service.response.walk.CompleteWalkResponse;

@RestController
@RequestMapping("/api/v1/walk")
@RequiredArgsConstructor
@Tag(name = "Walk API", description = "산책 API")
public class WalkController {

    private final WalkService walkService;

    @Operation(
            summary = "산책 완료",
            description = """
                    산책을 완료해 DB에 저장하고 관련한 소모 칼로리와 위도, 경도를 반환합니다.
                    요청 본문에 산책 시간 및 거리를 포함해야 합니다.
                    """
    )
    @PostMapping("/complete")
    public ApiResponse<CompleteWalkResponse> completeWalk(@AuthenticationPrincipal CustomOAuth2User oAuth2User,
                                                          @RequestBody @Valid CompleteWalkRequest request){

        return ApiResponse.ok(walkService.completeWalk(oAuth2User.getMember(), request.toServiceRequest()));
    }

}
