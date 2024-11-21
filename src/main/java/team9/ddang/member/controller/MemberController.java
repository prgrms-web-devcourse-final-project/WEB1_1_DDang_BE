package team9.ddang.member.controller;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;
import team9.ddang.global.api.ApiResponse;
import team9.ddang.member.controller.request.JoinRequest;
import team9.ddang.member.entity.Provider;
import team9.ddang.member.oauth2.CustomOAuth2User;
import team9.ddang.member.repository.MemberRepository;
import team9.ddang.member.service.MemberService;
import team9.ddang.member.service.response.MemberResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
@Slf4j
public class MemberController {

    private final MemberRepository memberRepository;
    private final MemberService memberService;

    @GetMapping("/sign-up")
    @PreAuthorize("hasRole('ROLE_GUEST')")
    public ApiResponse<String> signUpPage() {
        // ROLE_GUEST 사용자만 접근 가능
        return ApiResponse.ok("추가 정보를 입력해주세요.");
    }

//    @PostMapping("/join")
//    @PreAuthorize("hasRole('ROLE_GUEST')")
//    public ApiResponse<MemberResponse> join(@RequestBody @Valid JoinRequest request,
//                                            @AuthenticationPrincipal OAuth2User principal,
//                                            HttpServletResponse response) {
//
//        CustomOAuth2User customOAuth2User = (CustomOAuth2User) principal;
//        Provider provider = customOAuth2User.getMember().getProvider();
//        String email = customOAuth2User.getMember().getEmail();
//
//        return ApiResponse.created(memberService.join(request.toServiceRequest(email, provider), response));
//    }

    @PostMapping("/join")
    @PreAuthorize("hasRole('ROLE_GUEST')")
    public ApiResponse<MemberResponse> join(@RequestBody @Valid JoinRequest request,
                                            @AuthenticationPrincipal CustomOAuth2User customOAuth2User,
                                            HttpServletResponse response) {

        log.info("SecurityContextHolder 인증 정보: {}", SecurityContextHolder.getContext().getAuthentication());
        log.info("CustomOAuth2User: {}", customOAuth2User);

        if (customOAuth2User == null || customOAuth2User.getEmail() == null) {
            throw new IllegalArgumentException("인증된 사용자가 없습니다.");
        }

        String email = customOAuth2User.getEmail(); // OAuth2에서 제공된 이메일
        Provider provider = customOAuth2User.getProvider(); // OAuth2 Provider 정보

        // 회원가입 서비스 호출
        return ApiResponse.created(memberService.join(request.toServiceRequest(email, provider), response));
    }

}