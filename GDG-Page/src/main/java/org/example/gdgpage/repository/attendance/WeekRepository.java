package org.example.gdgpage.repository.attendance;

import jakarta.persistence.LockModeType;
import org.example.gdgpage.domain.attendance.Week;
import org.example.gdgpage.domain.auth.PartType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface WeekRepository extends JpaRepository<Week, Long> {

    List<Week> findAllByPartOrderByWeekNoAsc(PartType part);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select w from Week w where w.part = :part order by w.weekNo desc")
    List<Week> findLatestForUpdate(@Param("part") PartType part);
}
