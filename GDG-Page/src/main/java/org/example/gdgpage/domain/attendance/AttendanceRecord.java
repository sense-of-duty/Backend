package org.example.gdgpage.domain.attendance;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.gdgpage.domain.auth.User;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Entity
@Table(
        name = "attendance_records",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_record_week_user", columnNames = {"week_id", "user_id"})
        },
        indexes = {
                @Index(name = "idx_record_user", columnList = "user_id"),
                @Index(name = "idx_record_week", columnList = "week_id")
        }
)
public class AttendanceRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "week_id", nullable = false)
    private Week week;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private AttendanceStatus status;

    @Column(name = "checked_at")
    private LocalDateTime checkedAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "updated_by")
    private Long updatedBy;

    public void markPresent(LocalDateTime now) {
        this.status = AttendanceStatus.PRESENT;
        this.checkedAt = now;
        this.updatedAt = now;
    }

    public void adminUpdate(AttendanceStatus status, Long adminId, LocalDateTime now) {
        this.status = status;
        this.updatedBy = adminId;
        this.updatedAt = now;
    }
}
