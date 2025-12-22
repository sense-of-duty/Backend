package org.example.gdgpage.service.attendance;

import lombok.RequiredArgsConstructor;
import org.example.gdgpage.domain.attendance.AttendanceRecord;
import org.example.gdgpage.domain.attendance.AttendanceSession;
import org.example.gdgpage.domain.attendance.AttendanceStatus;
import org.example.gdgpage.domain.attendance.Week;
import org.example.gdgpage.domain.auth.PartType;
import org.example.gdgpage.domain.auth.User;
import org.example.gdgpage.dto.attendance.response.ActiveSessionResponse;
import org.example.gdgpage.dto.attendance.response.AttendanceCheckResponse;
import org.example.gdgpage.dto.attendance.response.MyAttendanceResponse;
import org.example.gdgpage.dto.attendance.response.MyAttendanceSummaryResponse;
import org.example.gdgpage.exception.BadRequestException;
import org.example.gdgpage.exception.ErrorMessage;
import org.example.gdgpage.exception.ForbiddenException;
import org.example.gdgpage.exception.NotFoundException;
import org.example.gdgpage.mapper.attendance.AttendanceMapper;
import org.example.gdgpage.repository.attendance.AttendanceRecordRepository;
import org.example.gdgpage.repository.attendance.WeekRepository;
import org.example.gdgpage.repository.auth.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AttendanceServiceImpl implements AttendanceService {

    private final UserRepository userRepository;
    private final WeekRepository weekRepository;
    private final AttendanceRecordRepository recordRepository;
    private final AttendanceSessionService sessionService;

    @Override
    @Transactional
    public AttendanceCheckResponse checkAttendance(Long userId, String code) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.NOT_EXIST_USER));

        PartType part = user.getPart();

        AttendanceSession active = sessionService.getActiveSessionEntityForPartOrNull(part);

        if (active == null) {
            throw new BadRequestException(ErrorMessage.ATTENDANCE_SESSION_NOT_ACTIVE);
        }

        if (!sessionService.matchesCode(active, code)) {
            throw new BadRequestException(ErrorMessage.ATTENDANCE_CODE_INVALID);
        }

        Week week = active.getWeek();

        recordRepository.findByWeekAndUser(week, user).ifPresent(record -> {
            if (record.getStatus() == AttendanceStatus.PRESENT) {
                throw new BadRequestException(ErrorMessage.ATTENDANCE_ALREADY_CHECKED);
            }
        });

        LocalDateTime now = LocalDateTime.now();

        AttendanceRecord record = recordRepository.findByWeekAndUser(week, user)
                .orElseGet(() -> AttendanceRecord.builder()
                        .week(week)
                        .user(user)
                        .status(AttendanceStatus.PRESENT)
                        .build());

        record.markPresent(now);
        AttendanceRecord saved = recordRepository.save(record);

        return AttendanceMapper.toAttendanceCheckResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public MyAttendanceResponse getMyAttendance(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.NOT_EXIST_USER));

        PartType part = user.getPart();

        List<Week> weeks = weekRepository.findAllByPartOrderByWeekNoAsc(part);

        if (weeks.isEmpty()) {
            return MyAttendanceResponse.builder()
                    .part(part)
                    .weeks(List.of())
                    .build();
        }

        List<Long> weekIds = weeks.stream()
                .map(Week::getId)
                .toList();

        List<AttendanceRecord> records = recordRepository.findAllByWeekIdsAndUserId(weekIds, userId);

        Map<Long, AttendanceRecord> map = records.stream()
                .collect(Collectors.toMap(record -> record.getWeek().getId(), record -> record));

        List<MyAttendanceSummaryResponse> summary = new ArrayList<>();
        for (Week week : weeks) {
            summary.add(
                    AttendanceMapper.toMySummary(week, map.get(week.getId()))
            );
        }

        return MyAttendanceResponse.builder()
                .part(part)
                .weeks(summary)
                .build();
    }

    @Override
    @Transactional
    public void adminUpdateStatus(Long adminId, Long weekId, Long targetUserId, AttendanceStatus status) {
        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.NOT_EXIST_USER));

        Week week = weekRepository.findById(weekId)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.NOT_EXIST_WEEK));

        if (week.getPart() != admin.getPart()) {
            throw new ForbiddenException(ErrorMessage.FORBIDDEN_PART_ACCESS);
        }

        User target = userRepository.findById(targetUserId)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.NOT_EXIST_USER));

        LocalDateTime now = LocalDateTime.now();

        AttendanceRecord record = recordRepository.findByWeekAndUser(week, target)
                .orElseGet(() -> AttendanceRecord.builder()
                        .week(week)
                        .user(target)
                        .status(status)
                        .build());

        record.adminUpdate(status, adminId, now);
        recordRepository.save(record);
    }

    @Override
    @Transactional(readOnly = true)
    public ActiveSessionResponse getActive(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.NOT_EXIST_USER));

        PartType part = user.getPart();

        return sessionService.getActiveSessionForPart(part);
    }
}
