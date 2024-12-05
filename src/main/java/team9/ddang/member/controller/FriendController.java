package team9.ddang.member.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
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
    @PostMapping("")
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
    @DeleteMapping("/{memberId}")
    public ApiResponse<Void> deleteFriend(@AuthenticationPrincipal CustomOAuth2User customOAuth2User,
                                       @PathVariable(value = "memberId") Long memberId){

        friendService.deleteFriend(customOAuth2User.getMember() ,memberId);
        return ApiResponse.noContent();
    }
}
