package team9.ddang.member.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;
import team9.ddang.IntegrationTestSupport;
import team9.ddang.dog.entity.Dog;
import team9.ddang.dog.entity.IsNeutered;
import team9.ddang.dog.entity.MemberDog;
import team9.ddang.dog.repository.DogRepository;
import team9.ddang.dog.repository.MemberDogRepository;
import team9.ddang.global.entity.Gender;
import team9.ddang.member.entity.*;
import team9.ddang.member.jwt.service.JwtService;
import team9.ddang.member.repository.MemberRepository;
import team9.ddang.member.repository.WalkWithMemberRepository;
import team9.ddang.member.service.request.JoinServiceRequest;
import team9.ddang.member.service.request.UpdateAddressServiceRequest;
import team9.ddang.member.service.request.UpdateServiceRequest;
import team9.ddang.member.service.response.MemberResponse;
import team9.ddang.member.service.response.MyPageResponse;
import team9.ddang.member.service.response.UpdateResponse;
import team9.ddang.walk.entity.Walk;
import team9.ddang.walk.repository.WalkRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@Transactional
class MemberServiceImplTest extends IntegrationTestSupport {

    @Autowired
    private MemberService memberService;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private WalkRepository walkRepository;

    @Autowired
    private WalkWithMemberRepository walkWithMemberRepository;

    @Autowired
    private DogRepository dogRepository;

    @Autowired
    private MemberDogRepository memberDogRepository;

    @MockBean
    private JwtService jwtService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @BeforeEach
    void setUp() {
        reset(jwtService);
    }

    @Test
    @DisplayName("회원 가입 테스트")
    public void join() {
        // given
        JoinServiceRequest request = new JoinServiceRequest(
                "john.doe@example.com",
                Provider.GOOGLE,
                "John Doe",
                Gender.MALE,
                "123 Main Street",
                FamilyRole.FATHER,
                "https://example.com/profile.jpg",
                IsMatched.TRUE,
                Role.USER
        );

        // when
        MemberResponse memberResponse = memberService.join(request, response);

        // then
        Member savedMember = memberRepository.findById(memberResponse.memberId()).orElseThrow();
        assertThat(savedMember).isNotNull();
        assertThat(savedMember.getEmail()).isEqualTo(request.email());
        assertThat(savedMember.getName()).isEqualTo(request.name());
        assertThat(savedMember.getProvider()).isEqualTo(request.provider());
        assertThat(savedMember.getRole()).isEqualTo(request.role());
    }

    @Test
    @DisplayName("Access Token 재발급 테스트")
    void reissueAccessToken() {

        // given
        String email = "john.doe@example.com";
        String oldRefreshToken = "valid-refresh-token";
        String newAccessToken = "new-access-token";
        String newRefreshToken = "new-refresh-token";

        // 회원 생성
        Member member = Member.builder()
                .name("John Doe")
                .email(email)
                .role(Role.USER)
                .isMatched(IsMatched.TRUE)
                .address("123 Main Street")
                .gender(Gender.MALE)
                .familyRole(FamilyRole.FATHER)
                .provider(Provider.GOOGLE)
                .profileImg("https://example.com/profile.jpg")
                .build();

        memberRepository.save(member);

        when(jwtService.extractRefreshTokenFromCookie(request)).thenReturn(Optional.of(oldRefreshToken));
        when(jwtService.isTokenValid(oldRefreshToken)).thenReturn(true);
        when(jwtService.extractEmailFromRefreshToken(oldRefreshToken)).thenReturn(Optional.of(email));
        when(jwtService.getRefreshTokenFromRedis(email)).thenReturn(Optional.of(oldRefreshToken));
        when(jwtService.createAccessToken(anyString(), anyString())).thenReturn(newAccessToken);
        when(jwtService.createRefreshToken(anyString())).thenReturn(newRefreshToken);

        doNothing().when(jwtService).sendAccessAndRefreshToken(response, newAccessToken, newRefreshToken);

        // when
        String result = memberService.reissueAccessToken(request, response);

        // then
        verify(jwtService, times(1)).extractRefreshTokenFromCookie(request);
        verify(jwtService, times(1)).isTokenValid(oldRefreshToken);
        verify(jwtService, times(1)).extractEmailFromRefreshToken(oldRefreshToken);
        verify(jwtService, times(1)).getRefreshTokenFromRedis(email);
        verify(jwtService, times(1)).createAccessToken(anyString(), anyString());
        verify(jwtService, times(1)).createRefreshToken(anyString());
        verify(jwtService, times(1)).sendAccessAndRefreshToken(response, newAccessToken, newRefreshToken);

        assertThat(result).isEqualTo(newAccessToken);
    }

    @Test
    @DisplayName("로그아웃 테스트")
    void logout() {
        // given
        String accessToken = "valid-access-token";
        String email = "john.doe@example.com";
        String refreshToken = "valid-refresh-token";

        when(jwtService.extractAccessToken(request)).thenReturn(Optional.of(accessToken));
        when(jwtService.isTokenValid(accessToken)).thenReturn(true);
        when(jwtService.extractEmail(accessToken)).thenReturn(Optional.of(email));
        when(jwtService.getRefreshTokenFromRedis(email)).thenReturn(Optional.of(refreshToken));

        doNothing().when(jwtService).removeRefreshTokenFromRedis(email);

        // when
        String result = memberService.logout(request);

        // then
        verify(jwtService, times(1)).extractAccessToken(request);  // Access Token 추출 메소드 호출 확인
        verify(jwtService, times(1)).isTokenValid(accessToken);   // Access Token 유효성 검사 메소드 호출 확인
        verify(jwtService, times(1)).extractEmail(accessToken);    // Access Token에서 이메일 추출 메소드 호출 확인
        verify(jwtService, times(1)).getRefreshTokenFromRedis(email);  // Redis에서 refresh token 조회 확인
        verify(jwtService, times(1)).removeRefreshTokenFromRedis(email);  // refresh token 삭제 메소드 호출 확인

        assertThat(result).isEqualTo("Success Logout");
    }

    @Test
    @DisplayName("회원 정보 조회 테스트")
    public void getMemberInfo() {
        // given
        Member member = Member.builder()
                .name("John Doe")
                .email("john.doe@example.com")
                .provider(Provider.GOOGLE)
                .gender(Gender.MALE)
                .address("123 Main Street")
                .familyRole(FamilyRole.FATHER)
                .profileImg("https://example.com/profile.jpg")
                .isMatched(IsMatched.TRUE)
                .role(Role.USER)
                .build();
        memberRepository.save(member);

        Walk walk1 = Walk.builder()
                .member(member)
                .startTime(LocalDateTime.now().minusHours(2))
                .endTime(LocalDateTime.now().minusHours(1))
                .totalDistance(5000) // 5,000 meters
                .walkImg("https://example.com/walk1.jpg")
                .build();
        Walk walk2 = Walk.builder()
                .member(member)
                .startTime(LocalDateTime.now().minusDays(1))
                .endTime(LocalDateTime.now().minusHours(20))
                .totalDistance(3000) // 3,000 meters
                .walkImg("https://example.com/walk2.jpg")
                .build();
        walkRepository.save(walk1);
        walkRepository.save(walk2);

        WalkWithMember walkWithMember = WalkWithMember.builder()
                .sender(member)
                .receiver(member)
                .build();
        walkWithMemberRepository.save(walkWithMember);

        Dog dog = Dog.builder()
                .name("Buddy")
                .breed("Golden Retriever")
                .birthDate(LocalDate.of(2018, 5, 20))
                .weight(new BigDecimal("30.5"))
                .gender(Gender.MALE)
                .profileImg("https://example.com/buddy.jpg")
                .isNeutered(IsNeutered.TRUE)
                .comment("Friendly dog")
                .build();
        dogRepository.save(dog);

        MemberDog memberDog = MemberDog.builder()
                .member(member)
                .dog(dog)
                .build();
        memberDogRepository.save(memberDog);

        // when
        MyPageResponse response = memberService.getMemberInfo(member.getMemberId());

        // then
        assertThat(response).isNotNull();
        assertThat(response.memberId()).isEqualTo(member.getMemberId());
        assertThat(response.name()).isEqualTo(member.getName());
        assertThat(response.email()).isEqualTo(member.getEmail());
        assertThat(response.address()).isEqualTo(member.getAddress());
        assertThat(response.gender()).isEqualTo(member.getGender());
        assertThat(response.familyRole()).isEqualTo(member.getFamilyRole());
        assertThat(response.profileImg()).isEqualTo(member.getProfileImg());
        assertThat(response.totalDistance()).isEqualTo(8.0);
        assertThat(response.walkCount()).isEqualTo(2);
        assertThat(response.countWalksWithMember()).isEqualTo(1);

        // Validate Dog Information
        assertThat(response.dog()).isNotNull();
        assertThat(response.dog().dogId()).isEqualTo(dog.getDogId());
        assertThat(response.dog().name()).isEqualTo(dog.getName());
        assertThat(response.dog().breed()).isEqualTo(dog.getBreed());
        assertThat(response.dog().birthDate()).isEqualTo(dog.getBirthDate());
        assertThat(response.dog().weight()).isEqualByComparingTo(dog.getWeight());
        assertThat(response.dog().gender()).isEqualTo(dog.getGender());
        assertThat(response.dog().profileImg()).isEqualTo(dog.getProfileImg());
        assertThat(response.dog().isNeutered()).isEqualTo(dog.getIsNeutered());
        assertThat(response.dog().familyId()).isNull();
        assertThat(response.dog().comment()).isEqualTo(dog.getComment());
    }

    @Test
    @DisplayName("강번따 상태 변경 테스트")
    public void updateIsMatched() {
        // given
        Member member = Member.builder()
                .name("Jane Doe")
                .email("jane.doe@example.com")
                .provider(Provider.GOOGLE)
                .gender(Gender.FEMALE)
                .address("456 Elm Street")
                .familyRole(FamilyRole.MOTHER)
                .profileImg("https://example.com/jane.jpg")
                .isMatched(IsMatched.TRUE)
                .role(Role.USER)
                .build();
        memberRepository.save(member);

        IsMatched newIsMatchedStatus = IsMatched.FALSE;

        // when
        IsMatched updatedStatus = memberService.updateIsMatched(member.getMemberId(), newIsMatchedStatus);

        // then
        assertThat(updatedStatus).isEqualTo(newIsMatchedStatus);

        Member updatedMember = memberRepository.findById(member.getMemberId())
                .orElseThrow(() -> new IllegalArgumentException("Member not found after update."));

        assertThat(updatedMember.getIsMatched()).isEqualTo(newIsMatchedStatus);
    }

    @Test
    @DisplayName("회원 정보 수정 테스트")
    public void updateMember() {
        // Given
        Member member = Member.builder()
                .name("Alice Smith")
                .email("alice.smith@example.com")
                .provider(Provider.GOOGLE)
                .gender(Gender.FEMALE)
                .address("789 Pine Street")
                .familyRole(FamilyRole.FATHER)
                .profileImg("https://example.com/alice.jpg")
                .isMatched(IsMatched.TRUE)
                .role(Role.USER)
                .build();
        memberRepository.save(member);

        UpdateServiceRequest updateRequest = new UpdateServiceRequest(
                "Alice Johnson",
                Gender.FEMALE,
                FamilyRole.MOTHER,
                "https://example.com/alice_new.jpg"
        );

        // When
        UpdateResponse updateResponse = memberService.updateMember(member.getMemberId(), updateRequest);

        // Then
        assertThat(updateResponse).isNotNull();
        assertThat(updateResponse.memberId()).isEqualTo(member.getMemberId());
        assertThat(updateResponse.name()).isEqualTo("Alice Johnson");
        assertThat(updateResponse.gender()).isEqualTo(Gender.FEMALE);
        assertThat(updateResponse.familyRole()).isEqualTo(FamilyRole.MOTHER);
        assertThat(updateResponse.profileImg()).isEqualTo("https://example.com/alice_new.jpg");

        Member updatedMember = memberRepository.findById(member.getMemberId())
                .orElseThrow(() -> new IllegalArgumentException("Member not found after update."));

        assertThat(updatedMember.getName()).isEqualTo("Alice Johnson");
        assertThat(updatedMember.getGender()).isEqualTo(Gender.FEMALE);
        assertThat(updatedMember.getFamilyRole()).isEqualTo(FamilyRole.MOTHER);
        assertThat(updatedMember.getProfileImg()).isEqualTo("https://example.com/alice_new.jpg");
    }

    @Test
    @DisplayName("회원 정보 수정 정보 조회 테스트")
    public void getUpdateInfo() {
        // Given
        Member member = Member.builder()
                .name("Charlie Brown")
                .email("charlie.brown@example.com")
                .provider(Provider.GOOGLE)
                .gender(Gender.MALE)
                .address("321 Oak Street")
                .familyRole(FamilyRole.FATHER)
                .profileImg("https://example.com/charlie.jpg")
                .isMatched(IsMatched.TRUE)
                .role(Role.USER)
                .build();
        memberRepository.save(member);

        // When
        UpdateResponse updateResponse = memberService.getUpdateInfo(member.getMemberId());

        // Then
        assertThat(updateResponse).isNotNull();
        assertThat(updateResponse.memberId()).isEqualTo(member.getMemberId());
        assertThat(updateResponse.name()).isEqualTo(member.getName());
        assertThat(updateResponse.gender()).isEqualTo(member.getGender());
        assertThat(updateResponse.familyRole()).isEqualTo(member.getFamilyRole());
        assertThat(updateResponse.profileImg()).isEqualTo(member.getProfileImg());
    }

    @Test
    @DisplayName("주소 변경 테스트")
    public void updateAddress() {
        // given
        Member member = Member.builder()
                .name("David Lee")
                .email("david.lee@example.com")
                .provider(Provider.GOOGLE)
                .gender(Gender.MALE)
                .address("Old Address")  // Initial address
                .familyRole(FamilyRole.FATHER)
                .profileImg("https://example.com/david.jpg")
                .isMatched(IsMatched.TRUE)
                .role(Role.USER)
                .build();
        memberRepository.save(member);

        String newAddress = "New Address";
        UpdateAddressServiceRequest updateRequest = new UpdateAddressServiceRequest(newAddress);

        // when
        memberService.updateAddress(member.getMemberId(), updateRequest);

        // then
        Member updatedMember = memberRepository.findById(member.getMemberId())
                .orElseThrow(() -> new IllegalArgumentException("Member not found after update."));

        assertThat(updatedMember.getAddress()).isEqualTo(newAddress);
    }
}