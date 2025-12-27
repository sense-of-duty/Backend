package org.example.gdgpage.service.attendance;

import lombok.RequiredArgsConstructor;
import org.example.gdgpage.domain.attendance.Week;
import org.example.gdgpage.domain.auth.PartType;
import org.example.gdgpage.dto.attendance.response.WeekCreateResponse;
import org.example.gdgpage.mapper.attendance.AttendanceMapper;
import org.example.gdgpage.repository.attendance.WeekRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AttendanceWeekServiceImpl implements AttendanceWeekService {

    private final WeekRepository weekRepository;

    @Override
    @Transactional
    public WeekCreateResponse createNextWeek(PartType part) {
        List<Week> latest = weekRepository.findLatestForUpdate(part);

        int nextWeekNo = latest.isEmpty() ? 1 : latest.get(0).getWeekNo() + 1;

        Week saved = weekRepository.save(
                Week.builder()
                        .part(part)
                        .weekNo(nextWeekNo)
                        .build()
        );

        return AttendanceMapper.toWeekCreateResponse(saved);
    }
}
