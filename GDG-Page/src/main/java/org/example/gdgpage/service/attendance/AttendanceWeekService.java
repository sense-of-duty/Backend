package org.example.gdgpage.service.attendance;

import lombok.RequiredArgsConstructor;
import org.example.gdgpage.domain.attendance.Week;
import org.example.gdgpage.dto.attendance.response.WeekCreateResponse;
import org.example.gdgpage.mapper.attendance.AttendanceMapper;
import org.example.gdgpage.repository.attendance.WeekRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AttendanceWeekService {

    private final WeekRepository weekRepository;

    @Transactional
    public WeekCreateResponse createNextWeek(Long courseId) {
        List<Week> latest = weekRepository.findLatestForUpdate(courseId);

        int nextWeekNo = latest.isEmpty() ? 1 : latest.get(0).getWeekNo() + 1;

        Week week = weekRepository.save(
                Week.builder()
                        .courseId(courseId)
                        .weekNo(nextWeekNo)
                        .build()
        );

        return AttendanceMapper.toWeekCreateResponse(week);
    }
}
