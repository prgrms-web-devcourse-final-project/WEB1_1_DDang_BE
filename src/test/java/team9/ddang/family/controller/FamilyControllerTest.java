//package team9.ddang.family.controller;
//
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.http.MediaType;
//import team9.ddang.ApiTestSupport;
//import team9.ddang.family.controller.request.FamilyCreateRequest;
//import team9.ddang.family.service.response.FamilyDetailResponse;
//import team9.ddang.family.service.response.FamilyResponse;
//import team9.ddang.family.service.response.InviteCodeResponse;
//import team9.ddang.member.entity.Role;
//import team9.ddang.security.WithMockPrincipalDetail;
//
//import java.util.List;
//
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.when;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//class FamilyControllerTest extends ApiTestSupport {
//
//    @Test
//    @WithMockPrincipalDetail(email = "test@example.com", role = Role.USER)
//    @DisplayName("POST /api/v1/family/register - 가족 생성")
//    void createFamily() throws Exception {
//        FamilyCreateRequest request = new FamilyCreateRequest("New Family");
//        FamilyResponse familyResponse = new FamilyResponse(1L, 1L, "Test Family");
//
//        when(familyService.createFamily(any(FamilyCreateRequest.class), any()))
//                .thenReturn(familyResponse);
//
//        mockMvc.perform(post("/api/v1/family/register")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(request)))
//                .andExpect(status().isCreated())
//                .andExpect(jsonPath("$.data.familyName").value("Test Family"))
//                .andExpect(jsonPath("$.data.memberId").value(1L));
//    }
//
//    @Test
//    @WithMockPrincipalDetail(email = "test@example.com", role = Role.USER)
//    @DisplayName("GET /api/v1/family/invite-code - 가족 초대 코드 생성")
//    void createInviteCode() throws Exception {
//        InviteCodeResponse inviteCodeResponse = new InviteCodeResponse(1L, "INVITE123", 300L);
//
//        when(familyService.createInviteCode(any()))
//                .thenReturn(inviteCodeResponse);
//
//        mockMvc.perform(get("/api/v1/family/invite-code"))
//                .andExpect(status().isCreated())
//                .andExpect(jsonPath("$.data.inviteCode").value("INVITE123"))
//                .andExpect(jsonPath("$.data.expiresInSeconds").value(300));
//    }
//
//    @Test
//    @WithMockPrincipalDetail(email = "test@example.com", role = Role.USER)
//    @DisplayName("POST /api/v1/family/join - 가족에 참여")
//    void joinFamily() throws Exception {
//        FamilyResponse familyResponse = new FamilyResponse(1L, 1L, "Test Family");
//
//        when(familyService.addMemberToFamily(any(String.class), any()))
//                .thenReturn(familyResponse);
//
//        mockMvc.perform(post("/api/v1/family/join")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content("\"INVITE123\""))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.data.familyName").value("Test Family"))
//                .andExpect(jsonPath("$.data.memberId").value(1L));
//    }
//
//    @Test
//    @WithMockPrincipalDetail(email = "test@example.com", role = Role.USER)
//    @DisplayName("GET /api/v1/family - 내 가족 정보 조회")
//    void getMyFamily() throws Exception {
//        FamilyDetailResponse familyDetailResponse = new FamilyDetailResponse(
//                1L,
//                1L,
//                "Test Family",
//                List.of(),
//                List.of()
//        );
//
//        when(familyService.getMyFamily(any()))
//                .thenReturn(familyDetailResponse);
//
//        mockMvc.perform(get("/api/v1/family"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.data.familyName").value("Test Family"));
//    }
//
//    @Test
//    @WithMockPrincipalDetail(email = "test@example.com", role = Role.USER)
//    @DisplayName("DELETE /api/v1/family/members/{memberId} - 가족 멤버 추방")
//    void removeMember() throws Exception {
//        mockMvc.perform(delete("/api/v1/family/members/1"))
//                .andExpect(status().isNoContent());
//    }
//
//    @Test
//    @WithMockPrincipalDetail(email = "test@example.com", role = Role.USER)
//    @DisplayName("DELETE /api/v1/family - 가족 삭제")
//    void deleteFamily() throws Exception {
//        mockMvc.perform(delete("/api/v1/family"))
//                .andExpect(status().isNoContent());
//    }
//
//    @Test
//    @WithMockPrincipalDetail(email = "test@example.com", role = Role.USER)
//    @DisplayName("DELETE /api/v1/family/leave - 가족 탈퇴")
//    void leaveFamily() throws Exception {
//        mockMvc.perform(delete("/api/v1/family/leave"))
//                .andExpect(status().isNoContent());
//    }
//}
