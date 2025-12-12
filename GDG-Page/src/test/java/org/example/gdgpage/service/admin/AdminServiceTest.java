package org.example.gdgpage.service.admin;

import org.example.gdgpage.domain.auth.PartType;
import org.example.gdgpage.domain.auth.Role;
import org.example.gdgpage.domain.auth.User;
import org.example.gdgpage.dto.admin.request.UpdateUserRequest;
import org.example.gdgpage.dto.admin.request.UserApproveRequest;
import org.example.gdgpage.dto.admin.request.UserRejectRequest;
import org.example.gdgpage.dto.user.response.UserResponse;
import org.example.gdgpage.exception.BadRequestException;
import org.example.gdgpage.exception.NotFoundException;
import org.example.gdgpage.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AdminService adminService;

    @Test
    void getAllUsers_모든유저를_UserResponse로_리턴한다() {
        // given
        User user1 = User.builder().id(1L).email("dnqls01@test.com").build();
        User user2 = User.builder().id(2L).email("eorud02@test.com").build();

        when(userRepository.findAll()).thenReturn(List.of(user1, user2));

        // when
        List<UserResponse> result = adminService.getAllUsers();

        // then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).id()).isEqualTo(1L);
        assertThat(result.get(1).id()).isEqualTo(2L);
        verify(userRepository).findAll();
    }

    @Test
    void getSignupRequests_미승인_요청만_조회한다() {
        // given
        User waitingUser = User.builder()
                .id(1L)
                .email("qhals05@test.com")
                .isApproved(false)
                .build();

        when(userRepository.findByIsApprovedFalseAndRejectionReasonIsNull())
                .thenReturn(List.of(waitingUser));

        // when
        List<UserResponse> result = adminService.getSignupRequests();

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).email()).isEqualTo("qhals05@test.com");
        verify(userRepository).findByIsApprovedFalseAndRejectionReasonIsNull();
    }

    @Test
    void updateUser_역할과_파트가_null이_아니면_업데이트한다() {
        // given
        User user = User.builder()
                .id(1L)
                .email("duswl03@test.com")
                .role(Role.MEMBER)
                .part(PartType.WEB)
                .build();

        UpdateUserRequest request = new UpdateUserRequest(Role.CORE, PartType.BACKEND);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        // when
        adminService.updateUser(1L, request);

        // then
        assertThat(user.getRole()).isEqualTo(Role.CORE);
        assertThat(user.getPart()).isEqualTo(PartType.BACKEND);
        verify(userRepository).findById(1L);
    }

    @Test
    void updateUser_존재하지_않으면_NotFoundException() {
        // given
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        UpdateUserRequest request = new UpdateUserRequest(Role.CORE, PartType.BACKEND);

        // when & then
        assertThatThrownBy(() -> adminService.updateUser(99L, request))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void kickUser_isActive를_false로_바꾼다() {
        // given
        User user = User.builder()
                .id(1L)
                .email("dnqls01@test.com")
                .isActive(true)
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        // when
        adminService.kickUser(1L);

        // then
        assertThat(user.isActive()).isFalse();
        verify(userRepository).findById(1L);
    }

    @Test
    void approveUsers_여러명_승인한다() {
        // given
        User user1 = User.builder().id(1L).isApproved(false).build();
        User user2 = User.builder().id(2L).isApproved(false).build();

        List<Long> ids = List.of(1L, 2L);
        when(userRepository.findAllById(ids)).thenReturn(List.of(user1, user2));

        UserApproveRequest request = new UserApproveRequest(ids);

        // when
        adminService.approveUsers(request);

        // then
        assertThat(user1.isApproved()).isTrue();
        assertThat(user2.isApproved()).isTrue();
        verify(userRepository).findAllById(ids);
    }

    @Test
    void approveUsers_요청수와_DB조회수가_다르면_BadRequestException() {
        // given
        User user1 = User.builder().id(1L).build();
        List<Long> ids = List.of(1L, 2L);

        when(userRepository.findAllById(ids)).thenReturn(List.of(user1));

        UserApproveRequest request = new UserApproveRequest(ids);

        // when & then
        assertThatThrownBy(() -> adminService.approveUsers(request))
                .isInstanceOf(BadRequestException.class);
    }

    @Test
    void rejectUsers_사유가_없으면_BadRequestException() {
        // given
        UserRejectRequest request = new UserRejectRequest(List.of(1L, 2L), "  ");

        // when & then
        assertThatThrownBy(() -> adminService.rejectUsers(request))
                .isInstanceOf(BadRequestException.class);
    }

    @Test
    void rejectUsers_여러명_반려하고_사유저장() {
        // given
        User user1 = User.builder().id(1L).isApproved(false).build();
        User user2 = User.builder().id(2L).isApproved(false).build();

        List<Long> ids = List.of(1L, 2L);
        when(userRepository.findAllById(ids)).thenReturn(List.of(user1, user2));

        UserRejectRequest request = new UserRejectRequest(ids, "파트 입력 오류");

        // when
        adminService.rejectUsers(request);

        // then
        assertThat(user1.isApproved()).isFalse();
        assertThat(user2.isApproved()).isFalse();
        assertThat(user1.getRejectionReason()).isEqualTo("파트 입력 오류");
        assertThat(user2.getRejectionReason()).isEqualTo("파트 입력 오류");
        verify(userRepository).findAllById(ids);
    }
}
