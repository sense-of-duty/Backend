package org.example.gdgpage.service.attendance;

import lombok.RequiredArgsConstructor;
import org.example.gdgpage.domain.attendance.AttendanceSession;
import org.example.gdgpage.domain.attendance.AttendanceSessionStatus;
import org.example.gdgpage.domain.attendance.Week;
import org.example.gdgpage.domain.auth.PartType;
import org.example.gdgpage.dto.attendance.response.ActiveSessionResponse;
import org.example.gdgpage.dto.attendance.response.AdminSessionStartResponse;
import org.example.gdgpage.exception.BadRequestException;
import org.example.gdgpage.exception.ErrorMessage;
import org.example.gdgpage.exception.NotFoundException;
import org.example.gdgpage.mapper.attendance.AttendanceMapper;
import org.example.gdgpage.repository.attendance.AttendanceSessionRepository;
import org.example.gdgpage.repository.attendance.WeekRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AttendanceSessionServiceImpl implements AttendanceSessionService {

    private static final int EXPIRE_MINUTES = 5;

    private final WeekRepository weekRepository;
    private final AttendanceSessionRepository sessionRepository;
    private final AttendanceCodeGenerator codeGenerator;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public AdminSessionStartResponse startSession(Long weekId, Long adminId) {
        Week week = weekRepository.findById(weekId)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.NOT_EXIST_WEEK));

        LocalDateTime now = LocalDateTime.now();

        sessionRepository.findActiveByWeekId(weekId, now).ifPresent(session -> {
            throw new BadRequestException(ErrorMessage.ATTENDANCE_SESSION_ALREADY_EXISTS);
        });

        String code = codeGenerator.generate3Digits();
        String hash = passwordEncoder.encode(code);

        AttendanceSession session = sessionRepository.save(
                AttendanceSession.builder()
                        .week(week)
                        .codeHash(hash)
                        .status(AttendanceSessionStatus.OPEN)
                        .openedAt(now)
                        .expiresAt(now.plusMinutes(EXPIRE_MINUTES))
                        .createdBy(adminId)
                        .build()
        );

        return AttendanceMapper.toAdminSessionStartResponse(session, code);
    }

    @Override
    @Transactional
    public void closeSession(Long sessionId) {
        AttendanceSession attendanceSession = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.NOT_EXIST_ATTENDANCE_SESSION));

        if (attendanceSession.getStatus() != AttendanceSessionStatus.OPEN || attendanceSession.getClosedAt() != null) {
            return;
        }

        attendanceSession.closeNow(LocalDateTime.now());
    }

    @Override
    @Transactional(readOnly = true)
    public ActiveSessionResponse getActiveSessionForPart(PartType part) {
        LocalDateTime now = LocalDateTime.now();

        return sessionRepository.findActiveByPart(part, now)
                .map(AttendanceMapper::toActiveSessionResponse)
                .orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public AttendanceSession getActiveSessionEntityForPartOrNull(PartType part) {
        LocalDateTime now = LocalDateTime.now();

        return sessionRepository.findActiveByPart(part, now)
                .orElse(null);
    }

    @Override
    public boolean matchesCode(AttendanceSession session, String rawCode) {
        return passwordEncoder.matches(rawCode, session.getCodeHash());
    }

    @Override
    @Transactional(readOnly = true)
    public AttendanceSession getSession(Long sessionId) {
        return sessionRepository.findById(sessionId)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.NOT_EXIST_ATTENDANCE_SESSION));
    }
}
