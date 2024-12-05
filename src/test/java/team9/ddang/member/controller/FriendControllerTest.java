package team9.ddang.member.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import team9.ddang.ApiTestSupport;
import team9.ddang.dog.entity.Dog;
import team9.ddang.dog.entity.IsNeutered;
import team9.ddang.global.entity.Gender;
import team9.ddang.member.controller.request.AddFriendRequest;
import team9.ddang.member.entity.*;
import team9.ddang.member.oauth2.CustomOAuth2User;
import team9.ddang.member.service.response.FriendListResponse;
import team9.ddang.member.service.response.FriendResponse;
import team9.ddang.member.service.response.MemberResponse;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@MockBean(JpaMetamodelMappingContext.class)
class FriendControllerTest extends ApiTestSupport {

    @Test
    @DisplayName("친구를 추가한다.")
    @WithMockUser
    void addFriend() throws Exception {
        //given
        Long memberId = 1L;
        AddFriendRequest request = new AddFriendRequest(memberId, "ACCPET");

        Member member = Member.builder()
                .memberId(1L)
                .name("mjk")
                .email("user@example.com")
                .role(Role.USER)
                .address("Incheon")
                .isMatched(IsMatched.TRUE)
                .gender(Gender.MALE)
                .provider(Provider.GOOGLE)
                .profileImg("profileImg1.png")
                .familyRole(FamilyRole.ELDER_BROTHER)
                .build();

        // Mock된 사용자 설정
        CustomOAuth2User customOAuth2User = new CustomOAuth2User(
                Collections.emptySet(),
                Map.of("email", "user@example.com"),
                "email",
              member
        );

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(customOAuth2User, null, customOAuth2User.getAuthorities())
        );
        MemberResponse response = MemberResponse.from(member);
        String accessToken = jwtService.createAccessToken(member.getEmail(), "GOOGLE");

        //when
        given(friendService.decideFriend(any(Member.class), eq(request)))
                .willReturn(response);


        //then
        mockMvc.perform(post("/api/v1/friend")
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("OK"))
                .andExpect(jsonPath("$.data.memberId").value(1L))
                .andExpect(jsonPath("$.data.email").value("user@example.com"))
                .andExpect(jsonPath("$.data.name").value("mjk"))
                .andExpect(jsonPath("$.data.provider").value("GOOGLE"))
                .andExpect(jsonPath("$.data.birthDate").value("1999-09-03"))
                .andExpect(jsonPath("$.data.gender").value("MALE"))
                .andExpect(jsonPath("$.data.address").value("Incheon"))
                .andExpect(jsonPath("$.data.familyRole").value("BROTHER"))
                .andExpect(jsonPath("$.data.profileImg").value("profileImg1.png"));
    }

    @Test
    @DisplayName("친구 리스트를 조회한다.")
    @WithMockUser
    void getFriendList() throws Exception {
        //given
        Long memberId = 1L;

        Member member = Member.builder()
                .memberId(2L)
                .name("mjk")
                .email("user@example.com")
                .role(Role.USER)
                .address("Incheon")
                .isMatched(IsMatched.TRUE)
                .gender(Gender.MALE)
                .provider(Provider.GOOGLE)
                .profileImg("profileImg1.png")
                .familyRole(FamilyRole.ELDER_BROTHER)
                .build();

        List<FriendListResponse> response = new ArrayList<>();
        FriendListResponse friendListResponse = FriendListResponse.from(member);
        response.add(friendListResponse);

        String accessToken = jwtService.createAccessToken("user@example.com", "GOOGLE");

        // Mock된 사용자 설정
        CustomOAuth2User customOAuth2User = new CustomOAuth2User(
                Collections.emptySet(),
                Map.of("email", "user@example.com"),
                "email",
                Member.builder()
                        .memberId(memberId)
                        .name("hhj")
                        .email("user2@example.com")
                        .isMatched(IsMatched.TRUE)
                        .build()
        );

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(customOAuth2User, null, customOAuth2User.getAuthorities())
        );

        //when
        given(friendService.getFriendList(any(Member.class)))
                .willReturn(response);

        //then
        mockMvc.perform(get("/api/v1/friend")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("OK"))
                .andExpect(jsonPath("$.data[0].memberId").value(2L))
                .andExpect(jsonPath("$.data[0].gender").value("MALE"))
                .andExpect(jsonPath("$.data[0].familyRole").value("BROTHER"));

    }

    @Test
    @DisplayName("친구의 프로필을 상세 조회한다.")
    @WithMockUser
    void getFriend() throws Exception {
        //given
        Long memberId = 1L;

        String accessToken = jwtService.createAccessToken("user@example.com", "GOOGLE");

        Member member = Member.builder()
                .memberId(2L)
                .name("mjk")
                .email("user@example.com")
                .role(Role.USER)
                .address("Incheon")
                .isMatched(IsMatched.TRUE)
                .gender(Gender.MALE)
                .provider(Provider.GOOGLE)
                .profileImg("profileImg1.png")
                .familyRole(FamilyRole.ELDER_BROTHER)
                .build();

        // Mock된 사용자 설정
        CustomOAuth2User customOAuth2User = new CustomOAuth2User(
                Collections.emptySet(),
                Map.of("email", "user@example.com"),
                "email",
                Member.builder()
                        .memberId(memberId)
                        .name("hhh")
                        .email("user2@example.com")
                        .isMatched(IsMatched.TRUE)
                        .build()
        );

        Dog dog = Dog.builder()
                .breed("말티즈")
                .birthDate(LocalDate.of(2023,1,1))
                .name("쪼꼬")
                .weight(BigDecimal.valueOf(3.0))
                .isNeutered(IsNeutered.TRUE)
                .profileImg("profile")
                .gender(Gender.MALE)
                        .build();

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(customOAuth2User, null, customOAuth2User.getAuthorities())
        );

        FriendResponse response = FriendResponse.of(member, dog, 30, 10, 10);

        //when
        given(friendService.getFriend(any(Member.class), eq(2L)))
                .willReturn(response);

        //then
        mockMvc.perform(get("/api/v1/friend/{memberId}", 2L) // PathVariable로 memberId 추가
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("OK"))
                .andExpect(jsonPath("$.data.memberId").value(2L))
                .andExpect(jsonPath("$.data.memberName").value("mjk"))
                .andExpect(jsonPath("$.data.address").value("Incheon"))
                .andExpect(jsonPath("$.data.memberGender").value("MALE"))
                .andExpect(jsonPath("$.data.familyRole").value("BROTHER"))
                .andExpect(jsonPath("$.data.memberProfileImg").value("profileImg1.png"))
                .andExpect(jsonPath("$.data.totalDistance").value("30.0"))
                .andExpect(jsonPath("$.data.walkCount").value("10"))
                .andExpect(jsonPath("$.data.countWalksWithMember").value("10"))
                .andExpect(jsonPath("$.data.dogId").isEmpty())
                .andExpect(jsonPath("$.data.dogName").value("쪼꼬"))
                .andExpect(jsonPath("$.data.dogBreed").value("말티즈"))
                .andExpect(jsonPath("$.data.dogAge").value("2"))
                .andExpect(jsonPath("$.data.dogWeight").value("3"))
                .andExpect(jsonPath("$.data.dogGender").value("MALE"))
                .andExpect(jsonPath("$.data.dogProfileImg").value("profile"))
                .andExpect(jsonPath("$.data.isNeutered").value("TRUE"));

    }

    @Test
    @DisplayName("친구를 삭제한다")
    @WithMockUser
    void deleteFriend() throws Exception {
        //given
        Long memberId = 1L;

        String accessToken = jwtService.createAccessToken("user@example.com", "GOOGLE");

        // Mock된 사용자 설정
        CustomOAuth2User customOAuth2User = new CustomOAuth2User(
                Collections.emptySet(),
                Map.of("email", "user@example.com"),
                "email",
                Member.builder()
                        .memberId(memberId)
                        .name("mjk")
                        .email("user@example.com")
                        .isMatched(IsMatched.TRUE)
                        .build()
        );

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(customOAuth2User, null, customOAuth2User.getAuthorities())
        );


        //when
        doNothing().when(friendService).deleteFriend(any(Member.class), eq(2L));


        //then
        mockMvc.perform(delete("/api/v1/friend/{memberId}", 2L) // PathVariable로 memberId 추가
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("204"))
                .andExpect(jsonPath("$.status").value("NO_CONTENT"))
                .andExpect(jsonPath("$.message").value("No Content"))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    @DisplayName("친구를 추가시 memberId는 필수 입니다.")
    @WithMockUser
    void addFriendWhenMemberIdIsNullThenThrowException() throws Exception {
        //given
        AddFriendRequest request = new AddFriendRequest(null, "ACCEPT");

        Member member = Member.builder()
                .memberId(1L)
                .name("mjk")
                .email("user@example.com")
                .role(Role.USER)
                .address("Incheon")
                .isMatched(IsMatched.TRUE)
                .gender(Gender.MALE)
                .provider(Provider.GOOGLE)
                .profileImg("profileImg1.png")
                .familyRole(FamilyRole.ELDER_BROTHER)
                .build();

        // Mock된 사용자 설정
        CustomOAuth2User customOAuth2User = new CustomOAuth2User(
                Collections.emptySet(),
                Map.of("email", "user@example.com"),
                "email",
                member
        );

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(customOAuth2User, null, customOAuth2User.getAuthorities())
        );
        MemberResponse response = MemberResponse.from(member);
        String accessToken = jwtService.createAccessToken(member.getEmail(), "GOOGLE");

        //when
        given(friendService.decideFriend(any(Member.class), eq(request)))
                .willReturn(response);

        //then
        mockMvc.perform(post("/api/v1/friend")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("memberId 는 필수 값 입니다."));
    }
}