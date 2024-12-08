package team9.ddang.member.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import team9.ddang.global.api.ApiResponse;
import team9.ddang.member.controller.request.AddFriendRequest;
import team9.ddang.member.oauth2.CustomOAuth2User;
import team9.ddang.member.service.FriendService;
import team9.ddang.member.service.response.FriendListResponse;
import team9.ddang.member.service.response.FriendResponse;
import team9.ddang.member.service.response.MemberResponse;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/friend")
@Tag(name = "Friend API", description = "친구 관련 API")
@Slf4j
public class FriendController {

    private final FriendService friendService;

    @PostMapping("")
    @Operation(
            summary = "친구 추가, 거절",
            description = """
                    친구 추가,거절 요청을 보냅니다. 상대도 이미 보냈으면 친구 추가를 진행합니다. 거절 시 친구 요청은 삭제됩니다.
                    decision 값은 ACCEPT 혹은 DENY 로 보내주세요
                    """,
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "친구를 추가하고자 하는 멤버의 ID",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AddFriendRequest.class)
                    )
            ),
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
                    description = "친구 신청 혹은 친구 맺기 성공",
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
                                            value = "{ \"code\": 400, \"status\": \"BAD_REQUEST\", \"message\": \"해당 맴버를 찾을 수 없습니다.\", \"data\": null }"
                                    ),
                                    @ExampleObject(
                                            name = "memberId 값이 없는 경우",
                                            value = "{ \"code\": 400, \"status\": \"BAD_REQUEST\", \"message\": \"memberId 는 필수 값 입니다.\", \"data\": null }"
                                    ),
                                    @ExampleObject(
                                            name = "ACCPET 혹은 DENY 필수 입니다.",
                                            value = "{ \"code\": 400, \"status\": \"BAD_REQUEST\", \"message\": \"ACCEPT 혹은 DENY 필수 입니다.\", \"data\": null }"
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
    public ApiResponse<MemberResponse> addFriend(@AuthenticationPrincipal CustomOAuth2User customOAuth2User,
                                                 @RequestBody @Valid AddFriendRequest addFriendRequest){

        MemberResponse response = friendService.decideFriend(customOAuth2User.getMember(), addFriendRequest);
        return ApiResponse.ok(response);
    }


    @Operation(
            summary = "친구 리스트 조회",
            description = "친구인 멤버들의 리스트를 조회 합니다.",
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
                    description = "친구 리스트 불러오기 성공",
                    useReturnTypeSchema = true
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
    public ApiResponse<List<FriendListResponse> > getFriendList(@AuthenticationPrincipal CustomOAuth2User customOAuth2User){

        List<FriendListResponse> response = friendService.getFriendList(customOAuth2User.getMember());
        return ApiResponse.ok(response);
    }

    @Operation(
            summary = "친구 상세 조회",
            description = "친구의 프로필을 상세 조회합니다.",
            parameters = {
                    @Parameter(
                            name = "Authorization",
                            description = "액세스 토큰",
                            required = true,
                            in = ParameterIn.HEADER,
                            schema = @Schema(type = "string", example = "Bearer eyJhbGciOiJIUzI1...")
                    ),
                    @Parameter(
                            name = "memberId",
                            description = "멤버 ID",
                            required = true,
                            in = ParameterIn.PATH,
                            schema = @Schema(type = "long", example = "12345")
                    )
            }
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "친구 상세정보 조회 성공",
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
                                            value = "{ \"code\": 400, \"status\": \"BAD_REQUEST\", \"message\": \"해당 맴버를 찾을 수 없습니다.\", \"data\": null }"
                                    ),
                                    @ExampleObject(
                                            name = "친구가 아닌 회원 조회",
                                            value = "{ \"code\": 400, \"status\": \"BAD_REQUEST\", \"message\": \"친구가 아닌 사람의 프로필은 볼 수 없습니다.\", \"data\": null }"
                                    ),
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
    @GetMapping("/{memberId}")
    public ApiResponse<FriendResponse> getFriend(@AuthenticationPrincipal CustomOAuth2User customOAuth2User,
                                    @PathVariable(value = "memberId") Long memberId){

        FriendResponse response = friendService.getFriend(customOAuth2User.getMember() ,memberId);
        return ApiResponse.ok(response);
    }

    @Operation(
            summary = "친구 삭제",
            description = "친구를 삭제합니다.",
            parameters = {
                    @Parameter(
                            name = "Authorization",
                            description = "액세스 토큰",
                            required = true,
                            in = ParameterIn.HEADER,
                            schema = @Schema(type = "string", example = "Bearer eyJhbGciOiJIUzI1...")
                    ),
                    @Parameter(
                            name = "memberId",
                            description = "멤버 ID",
                            required = true,
                            in = ParameterIn.PATH,
                            schema = @Schema(type = "long", example = "12345")
                    )
            }
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "친구 삭제 성공",
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
                                            value = "{ \"code\": 400, \"status\": \"BAD_REQUEST\", \"message\": \"해당 맴버를 찾을 수 없습니다.\", \"data\": null }"
                                    ),
                                    @ExampleObject(
                                            name = "친구가 아닌 회원 삭제",
                                            value = "{ \"code\": 400, \"status\": \"BAD_REQUEST\", \"message\": \"친구가 아닌 사람을 삭제할 수 없습니다.\", \"data\": null }"
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
    @DeleteMapping("/{memberId}")
    public ApiResponse<Void> deleteFriend(@AuthenticationPrincipal CustomOAuth2User customOAuth2User,
                                       @PathVariable(value = "memberId") Long memberId){

        friendService.deleteFriend(customOAuth2User.getMember() ,memberId);
        return ApiResponse.noContent();
    }
}
