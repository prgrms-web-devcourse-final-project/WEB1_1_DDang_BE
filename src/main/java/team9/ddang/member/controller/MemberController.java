package team9.ddang.member.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import team9.ddang.global.api.ApiResponse;
import team9.ddang.member.controller.request.JoinRequest;
import team9.ddang.member.controller.request.UpdateAddressRequest;
import team9.ddang.member.controller.request.UpdateRequest;
import team9.ddang.member.entity.IsMatched;
import team9.ddang.member.oauth2.CustomOAuth2User;
import team9.ddang.member.service.MemberService;
import team9.ddang.member.service.response.MemberResponse;
import team9.ddang.member.service.response.MyPageResponse;
import team9.ddang.member.service.response.UpdateResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/member")
@Tag(name = "Member API", description = "멤버 관련 API")
@Slf4j
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/join")
    @PreAuthorize("hasRole('ROLE_GUEST')")
    @Operation(
            summary = "회원가입",
            description = "OAuth2 로그인 후 /register로 리디렉션 후 추가 정보 기입 후 회원가입을 완료합니다.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "회원가입 정보",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = JoinRequest.class)
                    )
            )
    )
    public ApiResponse<MemberResponse> join(@RequestBody @Valid JoinRequest joinRequest,
                                            HttpServletResponse response) {

        return ApiResponse.created(memberService.join(joinRequest.toServiceRequest(), response));
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @PostMapping("/reissue")
    @Operation(
            summary = "AccessToken 재발급",
            description = "RefreshToken을 사용하여 새로운 AccessToken을 발급합니다."
    )
    public ApiResponse<String> reissue(HttpServletRequest request, HttpServletResponse response) {
        log.info("reissue() 메서드 진입");

        String newAccessToken = memberService.reissueAccessToken(request, response);
        log.info("새로운 AccessToken 생성: {}", newAccessToken);

        // ApiResponse 사용하여 응답 반환
        return ApiResponse.ok(newAccessToken);
    }

    @DeleteMapping("/logout")
    @Operation(
            summary = "로그아웃",
            description = "로그아웃을 수행합니다."
    )
    public ApiResponse<String> logout(HttpServletRequest request) {
        log.info("logout() 메서드 진입");
        return ApiResponse.ok(memberService.logout(request));
    }


    @GetMapping("/mypage")
    @Operation(
            summary = "회원 정보 조회",
            description = "회원 정보를 조회합니다."
    )
    public ApiResponse<MyPageResponse> getMemberInfo(@AuthenticationPrincipal CustomOAuth2User customOAuth2User) {

        Long memberId = customOAuth2User.getMember().getMemberId();
        return ApiResponse.ok(memberService.getMemberInfo(memberId));
    }

    @PatchMapping
    @Operation(
            summary = "강번따 허용 여부 수정",
            description = "강번따 허용 여부를 수정합니다.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "강번따 허용 여부",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = IsMatched.class)
                    )
            )
    )
    public ApiResponse<IsMatched> updateIsMatched(
            @AuthenticationPrincipal CustomOAuth2User customOAuth2User,
            @RequestParam IsMatched isMatched) {

        Long memberId = customOAuth2User.getMember().getMemberId();
        IsMatched updatedIsMatched = memberService.updateIsMatched(memberId, isMatched);
        return ApiResponse.ok(updatedIsMatched);
    }

    @GetMapping("/update")
    @Operation(
            summary = "회원 정보 수정을 위한 정보 조회",
            description = "회원 정보 수정을 위한 정보를 조회합니다."
    )
    public ApiResponse<UpdateResponse> getUpdateInfo(@AuthenticationPrincipal CustomOAuth2User customOAuth2User) {

        return ApiResponse.ok(memberService.getUpdateInfo(customOAuth2User.getMember().getMemberId()));
    }

    @PatchMapping("/update")
    @Operation(
            summary = "회원 정보 수정",
            description = "회원 정보를 수정합니다.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "수정할 회원 정보",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UpdateRequest.class)
                    )
            )
    )
    public ApiResponse<UpdateResponse> updateMember(@RequestBody @Valid UpdateRequest updateRequest,
                                                    @AuthenticationPrincipal CustomOAuth2User customOAuth2User) {

        return ApiResponse.ok(memberService.updateMember(customOAuth2User.getMember().getMemberId(), updateRequest.toServiceRequest()));
    }

    @DeleteMapping("/delete")
    @Operation(
            summary = "회원 삭제",
            description = "회원을 삭제합니다."
    )
    public ApiResponse<String> deleteMember(@AuthenticationPrincipal CustomOAuth2User customOAuth2User) {

        memberService.deleteMember(customOAuth2User.getMember());
        return ApiResponse.ok("회원 삭제 완료");
    }

    @PatchMapping("/update/address")
    @Operation(
            summary = "주소 수정",
            description = "주소를 수정합니다.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "수정할 주소",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UpdateAddressRequest.class)
                    )
            )
    )
    public ApiResponse<String> updateAddress(@RequestBody @Valid UpdateAddressRequest updateAddressRequest,
                                             @AuthenticationPrincipal CustomOAuth2User customOAuth2User) {

        memberService.updateAddress(customOAuth2User.getMember().getMemberId(), updateAddressRequest.toServiceRequest());
        return ApiResponse.ok("주소 수정 완료");
    }

    @GetMapping("{memberId}")
    @Operation(
            summary = "특정 회원 정보 조회",
            description = "특정 회원의 정보를 조회합니다.",
            parameters = {
                    @Parameter(
                            name = "memberId",
                            description = "멤버 ID",
                            required = true,
                            in = ParameterIn.PATH,
                            schema = @Schema(type = "long", example = "12345")
                    )
            }
    )
    public ApiResponse<MyPageResponse> getMemberInfoWithId(@PathVariable Long memberId) {
        return ApiResponse.ok(memberService.getMemberInfo(memberId));
    }
}