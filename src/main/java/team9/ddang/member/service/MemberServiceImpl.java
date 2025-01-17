package team9.ddang.member.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team9.ddang.dog.repository.MemberDogRepository;
import team9.ddang.dog.service.DogService;
import team9.ddang.dog.service.response.GetDogResponse;
import team9.ddang.family.entity.Family;
import team9.ddang.family.exception.FamilyExceptionMessage;
import team9.ddang.family.repository.WalkScheduleRepository;
import team9.ddang.family.service.FamilyService;
import team9.ddang.member.entity.IsMatched;
import team9.ddang.member.entity.Member;
import team9.ddang.member.exception.MemberExceptionMessage;
import team9.ddang.member.jwt.service.JwtService;
import team9.ddang.member.repository.FriendRepository;
import team9.ddang.member.repository.MemberRepository;
import team9.ddang.member.repository.WalkWithMemberRepository;
import team9.ddang.member.service.request.JoinServiceRequest;
import team9.ddang.member.service.request.UpdateAddressServiceRequest;
import team9.ddang.member.service.request.UpdateServiceRequest;
import team9.ddang.member.service.response.MemberResponse;
import team9.ddang.member.service.response.MyPageResponse;
import team9.ddang.member.service.response.UpdateResponse;
import team9.ddang.notification.service.NotificationSettingsService;
import team9.ddang.walk.repository.WalkRepository;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final WalkRepository walkRepository;
    private final WalkScheduleRepository walkScheduleRepository;
    private final MemberDogRepository memberDogRepository;
    private final WalkWithMemberRepository walkWithMemberRepository;
    private final DogService dogService;
    private final JwtService jwtService;
    private final FamilyService familyService;
    private final NotificationSettingsService notificationSettingsService;
    private final FriendRepository friendRepository;

    @Override
    public MemberResponse join(JoinServiceRequest serviceRequest, HttpServletResponse response) {

        Member member = serviceRequest.toEntity();

        memberRepository.save(member);
        notificationSettingsService.saveDefaultNotificationSettings(member);

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

    @Transactional(readOnly = true)
    @Override
    public MyPageResponse getMemberInfo(Long memberId) {

        Member member = memberRepository.findActiveById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));

        int totalDistanceInMeters = walkRepository.findTotalDistanceByMemberId(memberId);
        int countWalks = walkRepository.countWalksByMemberId(memberId);

        double totalDistanceInKilometers = totalDistanceInMeters / 1000.0;
        int countWalksWithMember = walkWithMemberRepository.countBySenderMemberId(memberId);

        GetDogResponse dogResponse = dogService.getDogByMemberId(memberId);

        return MyPageResponse.from(member, totalDistanceInKilometers, countWalks, countWalksWithMember, dogResponse);
    }

    @Override
    public IsMatched updateIsMatched(Long memberId, IsMatched isMatched) {
        Member member = memberRepository.findActiveById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));

        member.updateIsMatched(isMatched);
        return member.getIsMatched(); // 업데이트된 값을 반환
    }

    @Override
    public UpdateResponse updateMember(Long memberId, UpdateServiceRequest updateServiceRequest) {

        Member member = memberRepository.findActiveById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));

        updateServiceRequest.toEntity(member);

        return UpdateResponse.from(member);
    }

    @Transactional(readOnly = true)
    @Override
    public UpdateResponse getUpdateInfo(Long memberId) {

        Member member = memberRepository.findActiveById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));

        return UpdateResponse.from(member);
    }

    @Override
    public void deleteMember(Member member) {
        Member currentMember = findMemberByIdOrThrowException(member.getMemberId());

        if (currentMember.getFamily() == null) {
            throw new IllegalArgumentException(FamilyExceptionMessage.MEMBER_NOT_IN_FAMILY.getText());
        }
        Family family = currentMember.getFamily();

        if (family.getMember().getMemberId().equals(currentMember.getMemberId())) { // 가족의 대표일 경우
            familyService.deleteFamilyAndMembersAndDogs(family, currentMember);
        } else { // 가족 구성원인 경우
            walkScheduleRepository.deleteByMemberId(currentMember.getMemberId());
            memberDogRepository.softDeleteByMember(currentMember);

            currentMember.updateFamily(null);
        }
        notificationSettingsService.deleteNotificationSettings(currentMember.getMemberId());
        friendRepository.deleteByMemberId(currentMember.getMemberId());
        memberRepository.softDeleteById(currentMember.getMemberId());

        if (jwtService.getRefreshTokenFromRedis(currentMember.getEmail()).isPresent()) {
            jwtService.removeRefreshTokenFromRedis(currentMember.getEmail());
        }
    }

    @Override
    public void updateAddress(Long memberId, UpdateAddressServiceRequest serviceRequest) {
        Member member = memberRepository.findActiveById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));

        member.updateAddress(serviceRequest.address());
    }

    private Member findMemberByIdOrThrowException(Long id) {
        return memberRepository.findActiveById(id)
                .orElseThrow(() -> {
                    log.warn(">>>> {} : {} <<<<", id, MemberExceptionMessage.MEMBER_NOT_FOUND);
                    return new IllegalArgumentException(MemberExceptionMessage.MEMBER_NOT_FOUND.getText());
                });
    }
}
