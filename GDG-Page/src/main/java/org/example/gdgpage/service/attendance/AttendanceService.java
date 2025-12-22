package org.example.gdgpage.service.attendance;

import lombok.RequiredArgsConstructor;
import org.example.gdgpage.domain.attendance.AttendanceRecord;
import org.example.gdgpage.domain.attendance.AttendanceSession;
import org.example.gdgpage.domain.attendance.AttendanceStatus;
import org.example.gdgpage.domain.attendance.Week;
import org.example.gdgpage.domain.auth.User;
import org.example.gdgpage.dto.attendance.response.AttendanceCheckResponse;
import org.example.gdgpage.dto.attendance.response.MyAttendanceResponse;
import org.example.gdgpage.dto.attendance.response.MyAttendanceSummaryResponse;
import org.example.gdgpage.exception.BadRequestException;
import org.example.gdgpage.exception.ErrorMessage;
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
public class AttendanceService {

    private final WeekRepository weekRepository;
    private final AttendanceRecordRepository recordRepository;
    private final AttendanceSessionService sessionService;
    private final UserRepository userRepository;

    @Transactional
    public AttendanceCheckResponse checkAttendance(Long weekId, Long userId, String code) {
        Week week = weekRepository.findById(weekId)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.NOT_EXIST_WEEK));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.NOT_EXIST_USER));

        AttendanceSession active = sessionService.getActiveSessionForWeekOrNull(weekId);

        if (active == null) {
            throw new BadRequestException(ErrorMessage.ATTENDANCE_SESSION_NOT_ACTIVE);
        }

        if (!sessionService.matchesCode(active, code)) {
            throw new BadRequestException(ErrorMessage.ATTENDANCE_CODE_INVALID);
        }

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

        return AttendanceMapper.toAttendanceCheckResponse(week, saved);
    }

    @Transactional(readOnly = true)
    public MyAttendanceResponse getMyAttendance(Long courseId, Long userId) {
        List<Week> weeks = weekRepository.findAllByCourseIdOrderByWeekNoAsc(courseId);

        if (weeks.isEmpty()) {
            return MyAttendanceResponse.builder()
                    .courseId(courseId)
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
                .courseId(courseId)
                .weeks(summary)
                .build();
    }

    @Transactional
    public void adminUpdateStatus(Long weekId, Long targetUserId, AttendanceStatus status, Long adminId) {
        Week week = weekRepository.findById(weekId)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.NOT_EXIST_WEEK));

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
}
