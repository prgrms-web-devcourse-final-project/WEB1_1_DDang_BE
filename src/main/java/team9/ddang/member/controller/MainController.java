package team9.ddang.member.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "메인 API 조회 성공",
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
                                            name = "강아지를 소유하지 않은 경우",
                                            value = "{ \"code\": 400, \"status\": \"BAD_REQUEST\", \"message\": \"소유한 강아지를 찾을 수 없습니다.\", \"data\": null }"
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
    @GetMapping("")
    public ApiResponse<MainResponse> getMain(@AuthenticationPrincipal CustomOAuth2User customOAuth2User){
        MainResponse response = mainService.getMain(customOAuth2User.getMember());
        return ApiResponse.ok(response);
    }
}
