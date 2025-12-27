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
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Entity
@Table(
        name = "attendance_sessions",
        indexes = {
                @Index(name = "idx_session_week", columnList = "week_id"),
                @Index(name = "idx_session_status", columnList = "status"),
                @Index(name = "idx_session_expires", columnList = "expires_at")
        }
)
public class AttendanceSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "week_id", nullable = false)
    private Week week;

    @Column(name = "code_hash", nullable = false, length = 100)
    private String codeHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private AttendanceSessionStatus status;

    @Column(name = "opened_at", nullable = false)
    private LocalDateTime openedAt;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @Column(name = "closed_at")
    private LocalDateTime closedAt;

    @Column(name = "created_by", nullable = false)
    private Long createdBy;

    public boolean isActive(LocalDateTime now) {
        if (status != AttendanceSessionStatus.OPEN) {
            return false;
        }

        if (closedAt != null) {
            return false;
        }

        return expiresAt.isAfter(now);
    }

    public void closeNow(LocalDateTime now) {
        this.status = AttendanceSessionStatus.CLOSED;
        this.closedAt = now;
    }

    public void expireNow(LocalDateTime now) {
        this.status = AttendanceSessionStatus.EXPIRED;
        this.closedAt = now;
    }
}
