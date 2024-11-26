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
import team9.ddang.family.service.response.FamilyResponse;
import team9.ddang.family.service.response.InviteCodeResponse;
import team9.ddang.global.api.ApiResponse;
import team9.ddang.member.entity.Member;

@RestController
@RequestMapping("/api/v1/families")
@RequiredArgsConstructor
@Tag(name = "Family API", description = "가족 생성, 조회 및 삭제 API")
public class FamilyController {
    private final FamilyService familyService;

    @PostMapping
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
    public ApiResponse<FamilyResponse> createFamily(@RequestBody FamilyCreateRequest request, @AuthenticationPrincipal Member currentMember) {
        FamilyResponse response = familyService.createFamily(request, currentMember);
        return ApiResponse.created(response);
    }


    @GetMapping("/{familyId}/invite-code")
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
    public ApiResponse<InviteCodeResponse> createInviteCode(@PathVariable Long familyId) {
        InviteCodeResponse response = familyService.createInviteCode(familyId);
        return ApiResponse.created(response);
    }



}
