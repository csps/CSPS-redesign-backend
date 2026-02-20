package org.csps.backend.service;

import java.awt.print.Pageable;
import java.time.LocalDate;
import java.util.List;

import org.csps.backend.domain.dtos.request.EventSessionRequestDTO;
import org.csps.backend.domain.dtos.response.EventSessionResponseDTO;
import org.springframework.data.domain.Page;

public interface EventSessionService {
    
    /* create a session for an event */
    EventSessionResponseDTO createSession(Long eventId, EventSessionRequestDTO dto);
    
    /* get all sessions for an event */
    List<EventSessionResponseDTO> getEventSessions(Long eventId);
    
    /* get sessions for an event on a specific date */
    List<EventSessionResponseDTO> getEventSessionsByDate(Long eventId, LocalDate date);
    
    /* get session by id */
    EventSessionResponseDTO getSessionById(Long sessionId);
    
    /* update session status */
    EventSessionResponseDTO updateSessionStatus(Long sessionId, String status);
    
    /* get QR token for a session (for authenticated student check-in) */
    String getQRTokenForSession(Long sessionId);
    
    /* get QR token for student with participant verification */
    String getQRTokenForStudentCheckIn(Long sessionId, String jwtToken);

    // /* get EventSession by student ID */
    // Page<EventSessionResponseDTO> getEventSessionByStudentId(Pageable pageable, String studentId);
    
    /* validate if session is currently active */
    boolean isSessionActive(Long sessionId);
}
