package team9.ddang.member.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import team9.ddang.ApiTestSupport;
import team9.ddang.dog.entity.IsNeutered;
import team9.ddang.dog.service.response.GetDogResponse;
import team9.ddang.global.entity.Gender;
import team9.ddang.member.controller.request.JoinRequest;
import team9.ddang.member.controller.request.UpdateAddressRequest;
import team9.ddang.member.controller.request.UpdateRequest;
import team9.ddang.member.entity.FamilyRole;
import team9.ddang.member.entity.IsMatched;
import team9.ddang.member.entity.Member;
import team9.ddang.member.entity.Provider;
import team9.ddang.member.oauth2.CustomOAuth2User;
import team9.ddang.member.service.request.UpdateAddressServiceRequest;
import team9.ddang.member.service.response.MemberResponse;
import team9.ddang.member.service.response.MyPageResponse;
import team9.ddang.member.service.response.UpdateResponse;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@MockBean(JpaMetamodelMappingContext.class)
class MemberControllerTest extends ApiTestSupport {

    @Test
    @DisplayName("회원가입 요청에 대해 정상적으로 응답을 반환한다.")
    @WithMockUser(roles = "GUEST")
    void join_Success() throws Exception {
        // given
        JoinRequest joinRequest = new JoinRequest(
                "john.doe@example.com",
                Provider.GOOGLE,
                "John Doe",
                Gender.MALE,
                "123 Main Street",
                FamilyRole.FATHER,
                "https://example.com/profile.jpg"
        );

        HttpServletResponse response = mock(HttpServletResponse.class);

        MemberResponse memberResponse = new MemberResponse(
                1L,
                "John Doe",
                "john.doe@example.com",
                Provider.GOOGLE,
                Gender.MALE,
                "123 Main Street",
                FamilyRole.FATHER,
                "https://example.com/profile.jpg"
        );

        // memberService의 join 호출 시 memberResponse 반환
        when(memberService.join(any(), any())).thenReturn(memberResponse);

        // when & then
        mockMvc.perform(post("/api/v1/member/join")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(joinRequest))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.memberId").value(1L))
                .andExpect(jsonPath("$.data.name").value("John Doe"))
                .andExpect(jsonPath("$.data.email").value("john.doe@example.com"))
                .andExpect(jsonPath("$.data.provider").value("GOOGLE"))
                .andExpect(jsonPath("$.data.gender").value("MALE"))
                .andExpect(jsonPath("$.data.address").value("123 Main Street"))
                .andExpect(jsonPath("$.data.familyRole").value("FATHER"))
                .andExpect(jsonPath("$.data.profileImg").value("https://example.com/profile.jpg"));
    }

    @Test
    @DisplayName("회원가입 요청 시 필수 필드가 누락되면 400 Bad Request를 반환한다.")
    @WithMockUser(roles = "GUEST")
    void join_BadRequest_ValidationError() throws Exception {
        // given: Validation 오류를 일으킬 빈값들
        JoinRequest invalidRequest = new JoinRequest(
                "",
                Provider.GOOGLE,
                "",
                Gender.MALE,
                "",
                FamilyRole.FATHER,
                ""
        );

        // when & then
        mockMvc.perform(post("/api/v1/member/join")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(invalidRequest))
                        .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists());
    }

    @DisplayName("accessToken 재발급 - 성공")
    @Test
    @WithMockUser(roles = "USER")
    void reissueAccessToken_success() throws Exception {
        // 테스트를 위한 더미 데이터 설정
        String newAccessToken = "newAccessToken123";
        when(memberService.reissueAccessToken(any(), any())).thenReturn(newAccessToken);

        // 요청 보내고 응답 결과 검증
        mockMvc.perform(post("/api/v1/member/reissue")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer validRefreshToken"))  // 유효한 RefreshToken
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value(newAccessToken));  // ApiResponse 안의 data 필드 검증

        verify(memberService, times(1)).reissueAccessToken(any(), any());  // 메서드 호출 검증
    }

    @DisplayName("accessToken 재발급 - 유효하지 않은 RefreshToken")
    @Test
    @WithMockUser(roles = "USER")
    void reissueAccessToken_invalidRefreshToken() throws Exception {
        // 유효하지 않은 RefreshToken으로 예외 처리 검증
        when(memberService.reissueAccessToken(any(), any())).thenThrow(new IllegalArgumentException("유효하지 않은 RefreshToken입니다."));

        mockMvc.perform(post("/api/v1/member/reissue")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer invalidRefreshToken"))  // 유효하지 않은 RefreshToken
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("유효하지 않은 RefreshToken입니다."));
    }

    @DisplayName("accessToken 재발급 - 유저를 찾을 수 없음")
    @Test
    @WithMockUser(roles = "USER")
    void reissueAccessToken_userNotFound() throws Exception {
        // 유저를 찾을 수 없을 경우 예외 처리
        when(memberService.reissueAccessToken(any(), any())).thenThrow(new IllegalArgumentException("유저를 찾을 수 없습니다."));

        mockMvc.perform(post("/api/v1/member/reissue")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer validRefreshToken"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("유저를 찾을 수 없습니다."));
    }

    @DisplayName("로그아웃 - 성공")
    @Test
    @WithMockUser(roles = "USER")
    void testLogout_Success() throws Exception {
        String mockAccessToken = "mockAccessToken";
        // mock jwtService 행동 정의
        when(jwtService.extractAccessToken(any(HttpServletRequest.class))).thenReturn(Optional.of(mockAccessToken));
        when(jwtService.isTokenValid(mockAccessToken)).thenReturn(true);
        when(jwtService.extractEmail(mockAccessToken)).thenReturn(Optional.of("test@example.com"));
        when(jwtService.getRefreshTokenFromRedis(anyString())).thenReturn(Optional.empty());

        // mock memberService 행동 정의
        when(memberService.logout(any(HttpServletRequest.class))).thenReturn("Success Logout");

        mockMvc.perform(post("/api/v1/member/logout")
                        .header("Authorization", "Bearer " + mockAccessToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("OK"))
                .andExpect(jsonPath("$.data").value("Success Logout"));
    }

    @DisplayName("마이페이지 - 회원 정보 조회 성공")
    @Test
    @WithMockUser(roles = "USER")
    void getMemberInfo() throws Exception {
        // Given
        Long memberId = 1L;

        // Mock된 사용자 설정
        CustomOAuth2User customOAuth2User = new CustomOAuth2User(
                Collections.emptySet(),
                Map.of("email", "user@example.com"),
                "email",
                Member.builder()
                        .memberId(memberId)
                        .name("John Doe")
                        .email("user@example.com")
                        .isMatched(IsMatched.TRUE)
                        .build()
        );

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(customOAuth2User, null, customOAuth2User.getAuthorities())
        );

        // Mock된 강아지 정보
        GetDogResponse dogResponse = new GetDogResponse(
                1L,
                "Buddy",
                "Labrador",
                LocalDate.of(2018, 5, 10),
                BigDecimal.valueOf(3.0),
                Gender.MALE,
                "https://example.com/dog.jpg",
                IsNeutered.TRUE,
                1L,
                "Very playful dog"
        );

        MyPageResponse myPageResponse = new MyPageResponse(
                memberId,
                "John Doe",
                "test@naver.com",
                "123 Test Street",
                Gender.MALE,
                FamilyRole.FATHER,
                "https://example.com/profile.jpg",
                12.5,
                5,
                3,
                dogResponse
        );

        when(memberService.getMemberInfo(memberId)).thenReturn(myPageResponse);

        // When & Then
        mockMvc.perform(get("/api/v1/member/mypage")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer test-access-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.memberId").value(memberId))
                .andExpect(jsonPath("$.data.name").value("John Doe"))
                .andExpect(jsonPath("$.data.email").value("test@naver.com"))
                .andExpect(jsonPath("$.data.address").value("123 Test Street"))
                .andExpect(jsonPath("$.data.gender").value("MALE"))
                .andExpect(jsonPath("$.data.familyRole").value("FATHER"))
                .andExpect(jsonPath("$.data.profileImg").value("https://example.com/profile.jpg"))
                .andExpect(jsonPath("$.data.totalDistance").value(12.5))
                .andExpect(jsonPath("$.data.walkCount").value(5))
                .andExpect(jsonPath("$.data.countWalksWithMember").value(3))
                .andExpect(jsonPath("$.data.dog.dogId").value(1))
                .andExpect(jsonPath("$.data.dog.name").value("Buddy"))
                .andExpect(jsonPath("$.data.dog.breed").value("Labrador"))
                .andExpect(jsonPath("$.data.dog.profileImg").value("https://example.com/dog.jpg"))
                .andExpect(jsonPath("$.data.dog.isNeutered").value("TRUE"))
                .andExpect(jsonPath("$.data.dog.comment").value("Very playful dog"));
    }



    @DisplayName("강번따 허용 여부 수정 - 성공")
    @Test
    @WithMockUser(roles = "USER")
    void updateIsMatched() throws Exception {
        // Given
        Long memberId = 1L; // 인증된 사용자의 ID
        IsMatched isMatched = IsMatched.TRUE; // 요청 값
        IsMatched updatedIsMatched = IsMatched.TRUE; // Mock된 반환 값

        // Mock된 사용자 설정
        CustomOAuth2User customOAuth2User = new CustomOAuth2User(
                Collections.emptySet(),
                Map.of("email", "user@example.com"),
                "email",
                Member.builder()
                        .memberId(memberId)
                        .name("John Doe")
                        .email("user@example.com")
                        .isMatched(IsMatched.FALSE) // 기존 상태
                        .build()
        );

        // SecurityContext에 Mock 사용자 설정
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(customOAuth2User, null, customOAuth2User.getAuthorities())
        );

        String accessToken = jwtService.createAccessToken("user@example.com", "GOOGLE");

        when(memberService.updateIsMatched(memberId, isMatched)).thenReturn(updatedIsMatched);

        // When & Then
        mockMvc.perform(patch("/api/v1/member")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("isMatched", isMatched.name())
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value(isMatched.name()))
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("OK"));
    }

    @DisplayName("회원 정보 수정 - 성공")
    @Test
    @WithMockUser(roles = "USER")
    void updateMember_Success() throws Exception {
        // Given
        Long memberId = 1L; // 인증된 사용자의 ID
        UpdateRequest updateRequest = new UpdateRequest(
                "John Doe Updated",
                Gender.MALE,
                FamilyRole.FATHER,
                "https://example.com/updated-profile.jpg"
        );

        UpdateResponse updatedResponse = new UpdateResponse(
                memberId,
                "John Doe Updated",
                Gender.MALE,
                FamilyRole.FATHER,
                "https://example.com/updated-profile.jpg"
        );

        // Mock된 사용자 설정
        CustomOAuth2User customOAuth2User = new CustomOAuth2User(
                Collections.emptySet(),
                Map.of("email", "user@example.com"),
                "email",
                Member.builder()
                        .memberId(memberId)
                        .name("John Doe")
                        .email("user@example.com")
                        .isMatched(IsMatched.TRUE)
                        .build()
        );

        // SecurityContext에 Mock 사용자 설정
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(customOAuth2User, null, customOAuth2User.getAuthorities())
        );

        // memberService.updateMember 호출 시 updateResponse 반환
        when(memberService.updateMember(eq(memberId), any())).thenReturn(updatedResponse);

        // When & Then
        mockMvc.perform(patch("/api/v1/member/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest))
                        .header(HttpHeaders.AUTHORIZATION, "Bearer test-access-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.memberId").value(memberId))
                .andExpect(jsonPath("$.data.name").value("John Doe Updated"))
                .andExpect(jsonPath("$.data.gender").value("MALE"))
                .andExpect(jsonPath("$.data.familyRole").value("FATHER"))
                .andExpect(jsonPath("$.data.profileImg").value("https://example.com/updated-profile.jpg"));

        // memberService.updateMember가 정확히 1번 호출되었는지 확인
        verify(memberService, times(1)).updateMember(eq(memberId), any());
    }

    @DisplayName("회원 정보 수정 시 필수 필드 누락 - 400 Bad Request")
    @Test
    @WithMockUser(roles = "USER")
    void updateMember_BadRequest_ValidationError() throws Exception {
        // Given: 필수 필드가 누락된 요청 데이터
        UpdateRequest invalidUpdateRequest = new UpdateRequest(
                "test",
                Gender.MALE,
                FamilyRole.FATHER,
                "" // 프로필 이미지 URL이 비어있음
        );

        // When & Then
        mockMvc.perform(patch("/api/v1/member/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidUpdateRequest))
                        .header(HttpHeaders.AUTHORIZATION, "Bearer test-access-token"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.message").value("프로필 이미지를 입력해주세요."));
    }

    @DisplayName("회원 삭제 요청 시 정상적으로 처리된다.")
    @Test
    @WithMockUser(roles = "USER")
    void deleteMember_Success() throws Exception {
        // given
        Long memberId = 1L;

        // Mock된 사용자 설정
        CustomOAuth2User customOAuth2User = new CustomOAuth2User(
                Collections.emptySet(),
                Map.of("email", "user@example.com"),
                "email",
                Member.builder()
                        .memberId(memberId)
                        .name("John Doe")
                        .email("user@example.com")
                        .build()
        );

        // SecurityContext에 Mock 사용자 설정
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(customOAuth2User, null, customOAuth2User.getAuthorities())
        );

        // memberService의 deleteMember 호출 시 아무것도 반환하지 않도록 설정
        doNothing().when(memberService).deleteMember(any());

        // when & then
        mockMvc.perform(delete("/api/v1/member/delete")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer test-access-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value("회원 삭제 완료"))
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("OK"));

        verify(memberService, times(1)).deleteMember(any());
    }

    @DisplayName("회원 삭제 시, 삭제 과정 중 예외가 발생하면 처리된다.")
    @Test
    @WithMockUser(roles = "USER")
    void deleteMember_Exception() throws Exception {
        // given
        Long memberId = 1L;

        // Mock된 사용자 설정
        CustomOAuth2User customOAuth2User = new CustomOAuth2User(
                Collections.emptySet(),
                Map.of("email", "user@example.com"),
                "email",
                Member.builder()
                        .memberId(memberId)
                        .name("John Doe")
                        .email("user@example.com")
                        .build()
        );

        // SecurityContext에 Mock 사용자 설정
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(customOAuth2User, null, customOAuth2User.getAuthorities())
        );

        // 예외 발생하는 서비스 메서드 모킹
        doThrow(new IllegalArgumentException("회원 삭제 중 오류 발생")).when(memberService).deleteMember(any());

        // when & then
        mockMvc.perform(delete("/api/v1/member/delete")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer test-access-token"))
                .andExpect(status().isBadRequest())  // 예외 발생 시 400 Bad Request
                .andExpect(jsonPath("$.message").value("회원 삭제 중 오류 발생"));

        verify(memberService, times(1)).deleteMember(any());
    }

    @DisplayName("주소 수정 요청 시 정상적으로 처리된다.")
    @Test
    @WithMockUser(roles = "USER")
    void updateAddress_Success() throws Exception {
        // given
        Long memberId = 1L;
        String newAddress = "새로운 주소";

        UpdateAddressRequest updateAddressRequest = new UpdateAddressRequest(newAddress);

        // Mock된 사용자 설정
        CustomOAuth2User customOAuth2User = new CustomOAuth2User(
                Collections.emptySet(),
                Map.of("email", "user@example.com"),
                "email",
                Member.builder()
                        .memberId(memberId)
                        .name("John Doe")
                        .email("user@example.com")
                        .build()
        );

        // SecurityContext에 Mock 사용자 설정
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(customOAuth2User, null, customOAuth2User.getAuthorities())
        );

        // memberService의 updateAddress 호출 시 아무것도 반환하지 않도록 설정
        doNothing().when(memberService).updateAddress(eq(memberId), any(UpdateAddressServiceRequest.class));

        // when & then
        mockMvc.perform(patch("/api/v1/member/update/address")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"address\": \"" + newAddress + "\"}")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer test-access-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value("주소 수정 완료"))
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("OK"));

        verify(memberService, times(1)).updateAddress(eq(memberId), any(UpdateAddressServiceRequest.class));
    }

    @DisplayName("주소 수정 시, 유효하지 않은 주소 입력 시 예외가 발생한다.")
    @Test
    @WithMockUser(roles = "USER")
    void updateAddress_InvalidAddress_Exception() throws Exception {
        // given
        String invalidAddress = "";  // 빈 주소

        UpdateAddressRequest updateAddressRequest = new UpdateAddressRequest(invalidAddress);

        // when & then
        mockMvc.perform(patch("/api/v1/member/update/address")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"address\": \"" + invalidAddress + "\"}")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer test-access-token"))
                .andExpect(status().isBadRequest())  // 예외 발생 시 400 Bad Request
                .andExpect(jsonPath("$.message").value("주소를 입력해주세요"));

        verify(memberService, never()).updateAddress(any(), any());
    }

    @DisplayName("주소 수정 시, 사용자 찾을 수 없으면 예외가 발생한다.")
    @Test
    @WithMockUser(roles = "USER")
    void updateAddress_UserNotFound_Exception() throws Exception {
        // given
        Long invalidMemberId = 999L;  // 존재하지 않는 회원 ID
        String newAddress = "새로운 주소";

        UpdateAddressRequest updateAddressRequest = new UpdateAddressRequest(newAddress);

        // SecurityContext에 Mock 사용자 설정
        CustomOAuth2User customOAuth2User = new CustomOAuth2User(
                Collections.emptySet(),
                Map.of("email", "user@example.com"),
                "email",
                Member.builder()
                        .memberId(invalidMemberId)
                        .name("John Doe")
                        .email("user@example.com")
                        .build()
        );

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(customOAuth2User, null, customOAuth2User.getAuthorities())
        );

        // 예외가 발생하는 서비스 메서드 모킹
        doThrow(new IllegalArgumentException("유저를 찾을 수 없습니다.")).when(memberService)
                .updateAddress(eq(invalidMemberId), any(UpdateAddressServiceRequest.class));

        // when & then
        mockMvc.perform(patch("/api/v1/member/update/address")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"address\": \"" + newAddress + "\"}")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer test-access-token"))
                .andExpect(status().isBadRequest())  // 예외 발생 시 400 Bad Request
                .andExpect(jsonPath("$.message").value("유저를 찾을 수 없습니다."));

        verify(memberService, times(1)).updateAddress(eq(invalidMemberId), any(UpdateAddressServiceRequest.class));
    }

    @DisplayName("회원 정보 조회 - 성공")
    @Test
    @WithMockUser(roles = "USER")
    public void testGetMemberInfoWithId() throws Exception {
        // Given
        Long memberId = 12345L;
        MyPageResponse myPageResponse = new MyPageResponse(
                memberId,
                "John Doe",
                "test@naver.com",
                "123 Main Street",
                Gender.MALE,
                FamilyRole.FATHER,
                "https://example.com/profile.jpg",
                12.5,
                5,
                3,
                null
        );

        when(memberService.getMemberInfo(memberId)).thenReturn(myPageResponse);

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/member/{memberId}", memberId))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.memberId").value(memberId))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.name").value("John Doe"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.email").value("test@naver.com"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.address").value("123 Main Street"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.gender").value("MALE"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.familyRole").value("FATHER"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.profileImg").value("https://example.com/profile.jpg"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.totalDistance").value(12.5))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.walkCount").value(5))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.countWalksWithMember").value(3))
                .andDo(print());

        verify(memberService, times(1)).getMemberInfo(memberId);
    }
}