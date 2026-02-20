package org.csps.backend.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.csps.backend.domain.entities.EventSession;
import org.csps.backend.domain.enums.SessionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventSessionRepository extends JpaRepository<EventSession, Long> {
    
    /* get all sessions for an event */
    List<EventSession> findByEventEventId(Long eventId);
    
    /* get sessions for an event on a specific date */
    List<EventSession> findByEventEventIdAndSessionDate(Long eventId, LocalDate sessionDate);
    
    /* find session by QR token code */
    Optional<EventSession> findByQrTokenCode(String qrTokenCode);
    
    /* get active sessions for an event */
    List<EventSession> findByEventEventIdAndSessionStatus(Long eventId, SessionStatus status);
}
