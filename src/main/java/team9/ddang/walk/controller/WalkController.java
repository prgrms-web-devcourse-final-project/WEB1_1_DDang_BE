package team9.ddang.walk.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "산책 완료 성공",
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
                                            name = "이동 데이터가 없는 경우",
                                            value = "{ \"code\": 400, \"status\": \"BAD_REQUEST\", \"message\": \"산책이 정상적으로 이루어지지 않았습니다.\", \"data\": null }"
                                    ),
                                    @ExampleObject(
                                            name = "친구가 아닌 회원 조회",
                                            value = "{ \"code\": 400, \"status\": \"BAD_REQUEST\", \"message\": \"친구가 아닌 사람의 프로필은 볼 수 없습니다.\", \"data\": null }"
                                    ),
                                    @ExampleObject(
                                            name = "강아지를 소유하지 않은 경우",
                                            value = "{ \"code\": 400, \"status\": \"BAD_REQUEST\", \"message\": \"소유한 강아지를 찾을 수 없습니다.\", \"data\": null }"
                                    ),
                                    @ExampleObject(
                                            name = "산책 총 거리가 없는 경우",
                                            value = "{ \"code\": 400, \"status\": \"BAD_REQUEST\", \"message\": \"산책 총 거리가 존재해야 합니다.\", \"data\": null }"
                                    ),
                                    @ExampleObject(
                                            name = "산책 총 거리가 0보다 작은 경우",
                                            value = "{ \"code\": 400, \"status\": \"BAD_REQUEST\", \"message\": \"산책 총 거리는 0보다 커야 합니다.\", \"data\": null }"
                                    ),
                                    @ExampleObject(
                                            name = "산책 총 시간이 없는 경우",
                                            value = "{ \"code\": 400, \"status\": \"BAD_REQUEST\", \"message\": \"산책 총 시간이 존재해야 합니다.\", \"data\": null }"
                                    ),
                                    @ExampleObject(
                                            name = "산책 총 시간이 0보다 작은 경우",
                                            value = "{ \"code\": 400, \"status\": \"BAD_REQUEST\", \"message\": \"산책 총 시간은 0보다 커야 합니다.\", \"data\": null }"
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
    @PostMapping("/complete")
    public ApiResponse<CompleteWalkResponse> completeWalk(@AuthenticationPrincipal CustomOAuth2User oAuth2User,
                                                          @RequestPart @Valid CompleteWalkRequest request,
                                                          @RequestPart MultipartFile walkImgFile) throws IOException {

        return ApiResponse.ok(walkService.completeWalk(oAuth2User.getMember(), request.toServiceRequest(), walkImgFile));
    }

}
