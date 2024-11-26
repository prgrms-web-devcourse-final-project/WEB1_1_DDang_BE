package team9.ddang.family.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import team9.ddang.family.controller.request.FamilyCreateRequest;
import team9.ddang.family.service.FamilyService;
import team9.ddang.family.service.response.FamilyDetailResponse;
import team9.ddang.family.service.response.FamilyResponse;
import team9.ddang.family.service.response.InviteCodeResponse;
import team9.ddang.global.api.ApiResponse;
import team9.ddang.member.entity.Member;
import team9.ddang.member.oauth2.CustomOAuth2User;

@RestController
@RequestMapping("/api/v1/family")
@RequiredArgsConstructor
@Tag(name = "Family API", description = "가족 생성, 조회 및 삭제 API")
public class FamilyController {
    private final FamilyService familyService;

    @PostMapping("/register")
    @Operation(
            summary = "가족 생성",
            description = """
                    새로운 가족을 생성하고, 생성된 가족 정보를 반환합니다.
                    요청 본문에는 가족 이름(familyName)이 포함되어야 합니다.
                    """,
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "가족 생성 요청 데이터",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = FamilyCreateRequest.class)
                    )
            ),
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "200",
                            description = "가족 생성 성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = FamilyResponse.class)
                            )
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "400",
                            description = "잘못된 요청 데이터",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ApiResponse.class)
                            )
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "500",
                            description = "서버 오류",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ApiResponse.class)
                            )
                    )
            }
    )
    public ApiResponse<FamilyResponse> createFamily(@RequestBody FamilyCreateRequest request, @AuthenticationPrincipal CustomOAuth2User currentUser) {
        Member currentMember = currentUser.getMember();
        FamilyResponse response = familyService.createFamily(request, currentMember);
        return ApiResponse.created(response);
    }


    @GetMapping("/invite-code")
    @Operation(
            summary = "가족 초대 코드 생성",
            description = """
                    지정된 가족에 대한 5분 유효기간의 초대 코드를 생성합니다.
                    생성된 초대 코드는 반환되며, 5분 후에 만료됩니다.
                    이미 초대 코드가 있는 경우, 남은 유효기간과 함께 초대 코드를 반환합니다.
                    """,
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "200",
                            description = "초대 코드 생성 성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = InviteCodeResponse.class)
                            )
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "404",
                            description = "가족이 존재하지 않음",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ApiResponse.class)
                            )
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "500",
                            description = "서버 오류",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ApiResponse.class)
                            )
                    )
            }
    )
    public ApiResponse<InviteCodeResponse> createInviteCode(@AuthenticationPrincipal CustomOAuth2User currentUser) {
        Member currentMember = currentUser.getMember();
        InviteCodeResponse response = familyService.createInviteCode(currentMember);
        return ApiResponse.created(response);
    }

    @PostMapping("/join")
    @Operation(
            summary = "가족에 참여",
            description = """
                초대 코드를 입력하여 가족에 참여합니다.
                초대 코드가 유효하지 않거나 만료되었을 경우 오류를 반환합니다.
                """,
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "초대 코드 요청 데이터",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = String.class)
                    )
            ),
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "200",
                            description = "가족 참여 성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = FamilyResponse.class)
                            )
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "400",
                            description = "잘못된 요청 데이터 또는 초대 코드 만료",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ApiResponse.class)
                            )
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "500",
                            description = "서버 오류",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ApiResponse.class)
                            )
                    )
            }
    )
    public ApiResponse<FamilyResponse> joinFamily(@RequestBody String inviteCode,
                                                  @AuthenticationPrincipal CustomOAuth2User currentUser) {
        Member currentMember = currentUser.getMember();
        FamilyResponse response = familyService.addMemberToFamily(inviteCode, currentMember);
        return ApiResponse.ok(response);
    }

    @GetMapping
    @Operation(
            summary = "내 가족 정보 조회",
            description = """
                로그인한 사용자가 속한 가족 정보를 조회합니다.
                """,
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "200",
                            description = "가족 정보 조회 성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = FamilyDetailResponse.class)
                            )
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "404",
                            description = "가족 정보가 없습니다.",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ApiResponse.class)
                            )
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "500",
                            description = "서버 오류",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ApiResponse.class)
                            )
                    )
            }
    )
    public ApiResponse<FamilyDetailResponse> getMyFamily(@AuthenticationPrincipal CustomOAuth2User currentUser) {
        Member currentMember = currentUser.getMember();
        FamilyDetailResponse response = familyService.getMyFamily(currentMember);
        return ApiResponse.ok(response);
    }

}
