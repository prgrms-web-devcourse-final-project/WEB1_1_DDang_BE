package team9.ddang.member.oauth2.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import team9.ddang.member.entity.Role;
import team9.ddang.member.jwt.service.JwtService;
import team9.ddang.member.oauth2.CustomOAuth2User;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtService jwtService;

    //    @Override
//    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
//        log.info("OAuth2 Login 성공!");
//        try {
//            CustomOAuth2User oAuth2User = (CustomOAuth2User) authentication.getPrincipal();
//
//            if(oAuth2User.getMember().getRole() == Role.ROLE_GUEST) {
//                String accessToken = jwtService.createAccessToken(oAuth2User.getMember().getEmail());
//                response.addHeader(jwtService.getAccessHeader(), "Bearer " + accessToken);
//                log.info("accessToken : {}", accessToken);
//                response.sendRedirect("/api/v1/sign-up");
//
////                jwtService.sendAccessAndRefreshToken(response, accessToken, null);
//
////                Role을 GUEST -> USER로 업데이트하는 로직
////                지금은 회원가입 추가 폼 입력 시 업데이트하는 컨트롤러를 만들지 않아서 저렇게 놔둠
////                이후에 회원가입 추가 폼 입력 시 업데이트하는 컨트롤러, 서비스를 만들면 그 시점에 Role Update를 진행하면 될 것 같음
////                ▽▽▽▽▽▽▽▽▽▽▽▽▽▽▽▽▽▽▽▽▽▽▽▽▽▽▽▽▽▽▽▽▽▽▽▽▽▽▽▽▽▽▽▽▽▽▽▽▽▽▽▽▽▽▽▽▽▽▽▽▽▽▽▽▽▽▽▽▽▽▽▽▽▽▽▽▽▽▽▽▽▽▽▽▽▽▽▽▽▽▽
////                User findUser = userRepository.findByEmail(oAuth2User.getEmail())
////                                .orElseThrow(() -> new IllegalArgumentException("이메일에 해당하는 유저가 없습니다."));
////                findUser.authorizeUser();
//            } else {
//                loginSuccess(response, oAuth2User);
//            }
//        } catch (Exception e) {
//            throw e;
//        }
//
//    }
//
//    // TODO : 소셜 로그인 시에도 무조건 토큰 생성하지 말고 JWT 인증 필터처럼 RefreshToken 유/무에 따라 다르게 처리해보기
//    private void loginSuccess(HttpServletResponse response, CustomOAuth2User oAuth2User) throws IOException {
//        String accessToken = jwtService.createAccessToken(oAuth2User.getMember().getEmail());
//        String refreshToken = jwtService.createRefreshToken();
//        response.addHeader(jwtService.getAccessHeader(), "Bearer " + accessToken);
//        response.addHeader(jwtService.getRefreshHeader(), "Bearer " + refreshToken);
//
//        jwtService.sendAccessAndRefreshToken(response, accessToken, refreshToken);
//        jwtService.saveRefreshTokenToRedis(oAuth2User.getMember().getEmail(), refreshToken);
//    }
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        log.info("OAuth2 Login 성공!");
        try {
            CustomOAuth2User oAuth2User = (CustomOAuth2User) authentication.getPrincipal();

            Authentication auth = new UsernamePasswordAuthenticationToken(
                    oAuth2User, null, oAuth2User.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(auth);

            // ROLE_GUEST 상태로 추가 정보 입력 페이지로 리디렉션
            if (oAuth2User.isGuest()) {
                // ROLE_GUEST 상태 처리
                handleGuestLogin(response, oAuth2User);
            } else {
                // ROLE_USER 상태 처리
                handleUserLogin(response, oAuth2User);
            }
        } catch (Exception e) {
            log.error("OAuth2 로그인 처리 중 오류 발생: ", e);
            throw e;
        }
    }

    private void handleGuestLogin(HttpServletResponse response, CustomOAuth2User oAuth2User) throws IOException {
        log.info("GUEST 상태 - 추가 정보 입력 페이지로 리디렉션");

        String accessToken = jwtService.createAccessToken(oAuth2User.getEmail());
        response.addHeader(jwtService.getAccessHeader(), "Bearer " + accessToken);
        log.info("AccessToken 발급: {}", accessToken);

        // 추가 정보 입력 페이지로 리디렉션
        response.sendRedirect("/api/v1/sign-up");
    }

    private void handleUserLogin(HttpServletResponse response, CustomOAuth2User oAuth2User) throws IOException {
        log.info("USER 상태 - 로그인 성공 처리");
        String accessToken = jwtService.createAccessToken(oAuth2User.getMember().getEmail());
        String refreshToken = jwtService.createRefreshToken();

        response.addHeader(jwtService.getAccessHeader(), "Bearer " + accessToken);
        response.addHeader(jwtService.getRefreshHeader(), "Bearer " + refreshToken);

        jwtService.sendAccessAndRefreshToken(response, accessToken, refreshToken);
        jwtService.saveRefreshTokenToRedis(oAuth2User.getMember().getEmail(), refreshToken);

        log.info("AccessToken, RefreshToken 발급 및 저장 완료");
    }
}
