package team9.ddang.member.service;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team9.ddang.member.entity.Member;
import team9.ddang.member.entity.Role;
import team9.ddang.member.jwt.service.JwtService;
import team9.ddang.member.repository.MemberRepository;
import team9.ddang.member.service.request.JoinServiceRequest;
import team9.ddang.member.service.response.MemberResponse;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final JwtService jwtService;

    @Override
    public MemberResponse join(JoinServiceRequest serviceRequest, HttpServletResponse response) {
        Member member = serviceRequest.toEntity();

        String accessToken = jwtService.createAccessToken(member.getEmail());
        String refreshToken = jwtService.createRefreshToken();

        jwtService.sendAccessAndRefreshToken(response, accessToken, refreshToken);
        jwtService.saveRefreshTokenToRedis(refreshToken, member.getEmail());

        memberRepository.save(member);

        return MemberResponse.from(member);
    }
}
