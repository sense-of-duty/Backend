package org.example.gdgpage.repository.attendance;

import org.example.gdgpage.domain.attendance.Week;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WeekRepository extends JpaRepository<Week, Long> {
}
