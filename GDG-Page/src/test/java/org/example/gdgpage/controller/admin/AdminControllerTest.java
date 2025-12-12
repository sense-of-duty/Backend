package org.example.gdgpage.controller.admin;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.gdgpage.domain.auth.PartType;
import org.example.gdgpage.domain.auth.Role;
import org.example.gdgpage.dto.admin.request.UpdateUserRequest;
import org.example.gdgpage.dto.admin.request.UserApproveRequest;
import org.example.gdgpage.dto.admin.request.UserRejectRequest;
import org.example.gdgpage.dto.user.response.UserResponse;
import org.example.gdgpage.service.admin.AdminService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AdminController.class)
@AutoConfigureMockMvc(addFilters = false)
class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AdminService adminService;

    @Test
    @DisplayName("GET /admin/users - 전체 유저 조회")
    void getAllUsers() throws Exception {
        // given
        UserResponse user1 = UserResponse.builder()
                .id(1L)
                .email("dnqls01@test.com")
                .name("우빈")
                .role(Role.MEMBER)
                .part(PartType.WEB)
                .createdAt(LocalDateTime.now())
                .build();

        when(adminService.getAllUsers()).thenReturn(List.of(user1));

        // when & then
        mockMvc.perform(get("/admin/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].email").value("dnqls01@test.com"));

        verify(adminService).getAllUsers();
    }

    @Test
    @DisplayName("GET /admin/users/requests - 가입 요청 조회")
    void getSignupRequests() throws Exception {
        // given
        UserResponse user = UserResponse.builder()
                .id(2L)
                .email("dnqls01@test.com")
                .name("우빈")
                .role(Role.MEMBER)
                .isApproved(false)
                .build();

        when(adminService.getSignupRequests()).thenReturn(List.of(user));

        // when & then
        mockMvc.perform(get("/admin/users/requests"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(2L));

        verify(adminService).getSignupRequests();
    }

    @Test
    @DisplayName("PATCH /admin/users/{id}/role-part - 역할/파트 수정")
    void updateUser() throws Exception {
        // given
        UpdateUserRequest request = new UpdateUserRequest(Role.CORE, PartType.BACKEND);

        // when & then
        mockMvc.perform(patch("/admin/users/{userId}/role-part", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(adminService).updateUser(1L, request);
    }

    @Test
    @DisplayName("PATCH /admin/users/{id}/kick - 유저 강퇴")
    void expelUser() throws Exception {
        // when & then
        mockMvc.perform(patch("/admin/users/{userId}/kick", 1L))
                .andExpect(status().isNoContent());

        verify(adminService).kickUser(1L);
    }

    @Test
    @DisplayName("POST /admin/users/approve - 선택한 유저들 승인")
    void approveUsers() throws Exception {
        // given
        UserApproveRequest request = new UserApproveRequest(List.of(1L, 2L));

        // when & then
        mockMvc.perform(post("/admin/users/approve")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(adminService).approveUsers(any(UserApproveRequest.class));
    }

    @Test
    @DisplayName("POST /admin/users/reject - 선택한 유저들 반려")
    void rejectUsers() throws Exception {
        // given
        UserRejectRequest request = new UserRejectRequest(List.of(1L, 2L), "파트 입력 오류");

        // when & then
        mockMvc.perform(post("/admin/users/reject")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(adminService).rejectUsers(any(UserRejectRequest.class));
    }
}
