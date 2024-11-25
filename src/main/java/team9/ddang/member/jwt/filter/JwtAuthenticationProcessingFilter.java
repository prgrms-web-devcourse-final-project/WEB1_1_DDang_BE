package team9.ddang.member.jwt.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import team9.ddang.member.entity.Member;
import team9.ddang.member.jwt.service.JwtService;
import team9.ddang.member.oauth2.CustomOAuth2User;
import team9.ddang.member.repository.MemberRepository;

import java.io.IOException;
import java.util.*;

@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationProcessingFilter extends OncePerRequestFilter {

    private static final List<String> EXCLUDED_URLS = Arrays.asList(
            "/login", "/api/v1/member/join", "/api/v1/member/sign-up",
            "/api/v1/member/reissue", "/swagger", "/swagger-ui.html",
            "swagger-ui/index.html", "/swagger-ui", "/v3/api-docs");

    private final JwtService jwtService;
    private final MemberRepository memberRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        if (isExcludedUrl(request.getRequestURI())) {
            log.info("필터 제외 대상 요청: {}", request.getRequestURI());
            filterChain.doFilter(request, response);
            return;
        }

        String accessToken = jwtService.extractAccessToken(request)
                .filter(jwtService::isTokenValid)
                .orElse(null);

        if (accessToken != null) {
            checkAccessTokenAndAuthentication(accessToken, filterChain, request, response);
            return;
        }

        // AccessToken이 유효하지 않으면 클라이언트에 401 응답 전송
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().write("AccessToken is invalid");
    }

    private void checkAccessTokenAndAuthentication(String accessToken, FilterChain filterChain, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        jwtService.extractEmail(accessToken)
                .ifPresent(email -> memberRepository.findByEmail(email)
                        .ifPresent(this::saveAuthentication));

        filterChain.doFilter(request, response);
    }

    private void saveAuthentication(Member myMember) {
        CustomOAuth2User customOAuth2User = new CustomOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority(myMember.getRole().getKey())),
                Map.of("email", myMember.getEmail()),
                "email",
                myMember
        );

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                customOAuth2User,
                null,
                customOAuth2User.getAuthorities()
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        log.info("SecurityContext Authentication: {}", SecurityContextHolder.getContext().getAuthentication());
    }

    private boolean isExcludedUrl(String requestURI) {
        return EXCLUDED_URLS.stream().anyMatch(requestURI::startsWith);
    }
}


