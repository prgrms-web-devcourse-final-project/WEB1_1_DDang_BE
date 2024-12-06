package team9.ddang.walk.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import team9.ddang.global.api.ApiResponse;
import team9.ddang.member.oauth2.CustomOAuth2User;
import team9.ddang.walk.controller.request.walk.CompleteWalkRequest;
import team9.ddang.walk.service.WalkService;
import team9.ddang.walk.service.response.walk.CompleteWalkResponse;

import java.io.IOException;

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
                    응답 값에 같이 산책한 유저가 없으면 walkWithDogInfo 값이 null 입니다.
                    """,
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "반려견 등록 정보",
                    required = true,
                    content = @io.swagger.v3.oas.annotations.media.Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CompleteWalkRequest.class)
                    )
            ),
            parameters = {
                    @Parameter( name = "walkImgFile",
                            description = "Walk Image File",
                            required = true,
                            schema = @Schema(type = "string", format = "binary") ) }
    )
    @PostMapping("/complete")
    public ApiResponse<CompleteWalkResponse> completeWalk(@AuthenticationPrincipal CustomOAuth2User oAuth2User,
                                                          @RequestPart @Valid CompleteWalkRequest request,
                                                          @RequestPart MultipartFile walkImgFile) throws IOException {

        return ApiResponse.ok(walkService.completeWalk(oAuth2User.getMember(), request.toServiceRequest(), walkImgFile));
    }

}
