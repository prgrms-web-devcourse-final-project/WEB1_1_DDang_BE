package team9.ddang.member.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import team9.ddang.ApiTestSupport;
import team9.ddang.member.controller.request.JoinRequest;
import team9.ddang.global.entity.Gender;
import team9.ddang.member.entity.FamilyRole;
import team9.ddang.member.repository.MemberRepository;
import team9.ddang.member.service.MemberService;
import team9.ddang.member.service.response.MemberResponse;
import team9.ddang.member.service.response.MyPageResponse;

import java.time.LocalDate;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static team9.ddang.member.entity.Provider.GOOGLE;

@MockBean(JpaMetamodelMappingContext.class)
class MemberControllerTest extends ApiTestSupport {

    @DisplayName("회원가입 - 추가 정보 입력 후 회원가입 성공")
    @Test
    @WithMockUser(roles = "GUEST")
    void signUpAsGuest() throws Exception {
        // Given
        JoinRequest joinRequest = new JoinRequest(
                "guest@example.com",
                GOOGLE,
                "John Doe",
                LocalDate.of(1990, 1, 1),
                Gender.MALE,
                "123 Test Street",
                null,
                null
        );

        MemberResponse memberResponse = new MemberResponse(
                1L,
                "John Doe",
                "guest@example.com",
                GOOGLE,
                LocalDate.of(1990, 1, 1),
                Gender.MALE,
                "123 Test Street",
                null,
                null
        );

        String accessToken = jwtService.createAccessToken("guest@example.com", "GOOGLE");

        when(memberService.join(any(), any()))
                .thenReturn(memberResponse);

        // When & Then
        mockMvc.perform(post("/api/v1/member/join")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(joinRequest))
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value("John Doe"))
                .andExpect(jsonPath("$.data.email").value("guest@example.com"))
                .andExpect(jsonPath("$.data.birthDate").value("1990-01-01"));
    }

    @DisplayName("마이페이지 - 회원 정보 조회 성공")
    @Test
    @WithMockUser(roles = "USER")
    void getMemberInfo() throws Exception {
        // Given
        Long memberId = 1L;

        MyPageResponse myPageResponse = new MyPageResponse(
                memberId,
                "John Doe",
                "123 Test Street",
                Gender.MALE,
                FamilyRole.FATHER,
                12.5,
                5,
                3
        );

        when(memberService.getMemberInfo(memberId)).thenReturn(myPageResponse);

        // When & Then
        mockMvc.perform(get("/api/v1/member/{memberId}", memberId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer test-access-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.memberId").value(memberId))
                .andExpect(jsonPath("$.data.name").value("John Doe"))
                .andExpect(jsonPath("$.data.address").value("123 Test Street"))
                .andExpect(jsonPath("$.data.gender").value("MALE"))
                .andExpect(jsonPath("$.data.familyRole").value("FATHER"))
                .andExpect(jsonPath("$.data.totalDistance").value(12.5))
                .andExpect(jsonPath("$.data.walkCount").value(5))
                .andExpect(jsonPath("$.data.countWalksWithMember").value(3));
    }
}