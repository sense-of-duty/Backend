package org.example.gdgpage.service.attendance;

import lombok.RequiredArgsConstructor;
import org.example.gdgpage.domain.attendance.AttendanceSession;
import org.example.gdgpage.domain.attendance.AttendanceStatus;
import org.example.gdgpage.domain.attendance.Week;
import org.example.gdgpage.domain.auth.PartType;
import org.example.gdgpage.domain.auth.User;
import org.example.gdgpage.dto.attendance.response.AdminSessionStartResponse;
import org.example.gdgpage.dto.attendance.response.WeekCreateResponse;
import org.example.gdgpage.exception.BadRequestException;
import org.example.gdgpage.exception.ErrorMessage;
import org.example.gdgpage.exception.ForbiddenException;
import org.example.gdgpage.exception.NotFoundException;
import org.example.gdgpage.repository.attendance.WeekRepository;
import org.example.gdgpage.repository.auth.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AttendanceAdminService {

    private final UserRepository userRepository;
    private final WeekRepository weekRepository;
    private final AttendanceWeekService weekService;
    private final AttendanceSessionService sessionService;
    private final AttendanceService attendanceService;

    @Transactional
    public WeekCreateResponse createWeek(Long adminId) {
        PartType part = getUserPart(adminId);
        return weekService.createNextWeek(part);
    }

    @Transactional
    public AdminSessionStartResponse startSession(Long adminId, Long weekId) {
        PartType part = getUserPart(adminId);

        Week week = weekRepository.findById(weekId)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.NOT_EXIST_WEEK));

        if (week.getPart() != part) {
            throw new ForbiddenException(ErrorMessage.FORBIDDEN_PART_ACCESS);
        }

        return sessionService.startSession(weekId, adminId);
    }

    @Transactional
    public void closeSession(Long adminId, Long sessionId) {
        PartType part = getUserPart(adminId);

        AttendanceSession session = sessionService.getSession(sessionId);

        Week week = session.getWeek();

        if (week.getPart() != part) {
            throw new ForbiddenException(ErrorMessage.FORBIDDEN_PART_ACCESS);
        }

        sessionService.closeSession(sessionId);
    }

    @Transactional
    public void updateStatus(Long adminId, Long weekId, Long targetUserId, AttendanceStatus status) {
        attendanceService.adminUpdateStatus(adminId, weekId, targetUserId, status);
    }

    private User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.NOT_EXIST_USER));
    }

    private PartType getUserPart(Long adminId) {
        User admin = getUser(adminId);
        return admin.getPart();
    }
}
