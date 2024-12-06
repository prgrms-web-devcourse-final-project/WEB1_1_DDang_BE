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
import team9.ddang.member.entity.FamilyRole;
import team9.ddang.member.entity.IsMatched;
import team9.ddang.member.entity.Member;
import team9.ddang.member.oauth2.CustomOAuth2User;
import team9.ddang.member.service.response.MainResponse;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@MockBean(JpaMetamodelMappingContext.class)
class MainControllerTest extends ApiTestSupport {

    @Test
    @DisplayName("메인 화면을 조회한다.")
    @WithMockUser
    void getMain() throws Exception {
        //given
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
                        .familyRole(FamilyRole.ELDER_BROTHER)
                        .isMatched(IsMatched.TRUE)
                        .build()
        );

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(customOAuth2User, null, customOAuth2User.getAuthorities())
        );

        String accessToken = jwtService.createAccessToken(customOAuth2User.getName(), "GOOGLE");

        Dog dog = Dog.builder()
                .name("코코")
                .weight(BigDecimal.valueOf(3.3))
                .build();

        MainResponse mainResponse = MainResponse.of(customOAuth2User.getMember(), dog, 3600, 3000);

        //when

        given(mainService.getMain(any(Member.class)))
                .willReturn(mainResponse);

        //then
        mockMvc.perform(get("/api/v1/main")
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("OK"))
                .andExpect(jsonPath("$.data.memberId").value(1L))
                .andExpect(jsonPath("$.data.familyRole").value("ELDER_BROTHER"))
                .andExpect(jsonPath("$.data.dogName").value("코코"))
                .andExpect(jsonPath("$.data.timeDuration.hours").value("1"))
                .andExpect(jsonPath("$.data.totalDistanceMeter").value("3000"))
                .andExpect(jsonPath("$.data.totalCalorie").value("6"));

    }
}