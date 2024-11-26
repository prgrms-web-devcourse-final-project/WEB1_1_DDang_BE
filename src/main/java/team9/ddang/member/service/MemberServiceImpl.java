package team9.ddang.member.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team9.ddang.member.entity.Member;
import team9.ddang.member.jwt.service.JwtService;
import team9.ddang.member.repository.MemberRepository;
import team9.ddang.member.repository.WalkWithMemberRepository;
import team9.ddang.member.service.request.JoinServiceRequest;
import team9.ddang.member.service.response.MemberResponse;
import team9.ddang.member.service.response.MyPageResponse;
import team9.ddang.walk.repository.WalkRepository;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final WalkRepository walkRepository;
    private final WalkWithMemberRepository walkWithMemberRepository;
    private final JwtService jwtService;

    @Override
    public MemberResponse join(JoinServiceRequest serviceRequest, HttpServletResponse response) {

        Member member = serviceRequest.toEntity();

        memberRepository.save(member);

        String accessToken = jwtService.createAccessToken(member.getEmail(), member.getProvider().name());
        String refreshToken = jwtService.createRefreshToken(member.getEmail());

        jwtService.sendAccessAndRefreshToken(response, accessToken, refreshToken);
        jwtService.saveRefreshTokenToRedis(member.getEmail(), refreshToken);

        return MemberResponse.from(member);
    }

    @Override
    public String reissueAccessToken(HttpServletRequest request, HttpServletResponse response) {

        String refreshToken = jwtService.extractRefreshTokenFromCookie(request)
                .filter(jwtService::isTokenValid)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 RefreshToken입니다."));

        String email = jwtService.extractEmailFromRefreshToken(refreshToken)
                .orElseThrow(() -> new IllegalArgumentException("RefreshToken에서 email을 추출할 수 없습니다."));

        jwtService.getRefreshTokenFromRedis(email)
                .filter(storedToken -> storedToken.equals(refreshToken))
                .orElseThrow(() -> new IllegalArgumentException("Redis에서 RefreshToken이 유효하지 않습니다."));

        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));

        String newAccessToken = jwtService.createAccessToken(member.getEmail(), member.getProvider().name());
        String newRefreshToken = jwtService.createRefreshToken(member.getEmail());

        jwtService.removeRefreshTokenFromRedis(email); // 기존 토큰 삭제
        jwtService.saveRefreshTokenToRedis(member.getEmail(), newRefreshToken); // 새로운 토큰 저장

        jwtService.sendAccessAndRefreshToken(response, newAccessToken, newRefreshToken);

        return newAccessToken;
    }

    @Override
    public String logout(HttpServletRequest request) {

        String accessToken = jwtService.extractAccessToken(request)
                .filter(jwtService::isTokenValid)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 AccessToken입니다."));

        String email = jwtService.extractEmail(accessToken)
                .orElseThrow(() -> new IllegalArgumentException("AccessToken에서 email을 추출할 수 없습니다."));

        if (jwtService.getRefreshTokenFromRedis(email).isPresent()) {
            jwtService.removeRefreshTokenFromRedis(email);
        }

        return "Success Logout";
    }

    @Override
    public MyPageResponse getMemberInfo(Long memberId) {

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));

        int totalDistanceInMeters = walkRepository.findTotalDistanceByMemberId(memberId);
        int countWalks = walkRepository.countWalksByMemberId(memberId);

        double totalDistanceInKilometers = totalDistanceInMeters / 1000.0;

        int countWalksWithMember = walkWithMemberRepository.countBySenderMemberId(memberId);

        return MyPageResponse.from(member, totalDistanceInKilometers, countWalks, countWalksWithMember);
    }
}
