package org.csps.backend.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import org.csps.backend.domain.dtos.request.EventSessionRequestDTO;
import org.csps.backend.domain.dtos.response.EventSessionResponseDTO;
import org.csps.backend.domain.entities.Event;
import org.csps.backend.domain.entities.EventSession;
import org.csps.backend.domain.enums.SessionStatus;
import org.csps.backend.exception.EventNotFoundException;
import org.csps.backend.exception.EventSessionNotFoundException;
import org.csps.backend.exception.InvalidQRTokenException;
import org.csps.backend.exception.StudentNotParticipantException;
import org.csps.backend.mapper.EventSessionMapper;
import org.csps.backend.repository.EventParticipantRepository;
import org.csps.backend.repository.EventRepository;
import org.csps.backend.repository.EventSessionRepository;
import org.csps.backend.security.JwtService;
import org.csps.backend.service.EventSessionService;
import org.csps.backend.service.QRTokenService;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EventSessionServiceImpl implements EventSessionService {

    private final EventSessionRepository eventSessionRepository;
    private final EventRepository eventRepository;
    private final EventParticipantRepository eventParticipantRepository;
    private final EventSessionMapper eventSessionMapper;
    private final QRTokenService qrTokenService;
    private final JwtService jwtService;

    @Override
    @Transactional
    public EventSessionResponseDTO createSession(Long eventId, EventSessionRequestDTO dto) {
        /* verify event exists */
        Event event = eventRepository.findById(eventId)
            .orElseThrow(() -> new EventNotFoundException("Event not found with ID: " + eventId));

        /* validate time range */
        if (dto.getStartTime().isAfter(dto.getEndTime()) || dto.getStartTime().equals(dto.getEndTime())) {
            throw new IllegalArgumentException("Start time must be before end time");
        }

        /* generate unique QR token for this session (admin-created, no student ID) */
        String qrToken = qrTokenService.generateQRToken(eventId, "");

        /* create session */
        EventSession session = EventSession.builder()
            .event(event)
            .sessionName(dto.getSessionName())
            .sessionDate(dto.getSessionDate())
            .startTime(dto.getStartTime())
            .endTime(dto.getEndTime())
            .sessionStatus(SessionStatus.PENDING)
            .qrTokenCode(qrToken)
            .build();

        EventSession savedSession = eventSessionRepository.save(session);
        return eventSessionMapper.toResponseDTO(savedSession);
    }

    @Override
    public List<EventSessionResponseDTO> getEventSessions(Long eventId) {
        /* verify event exists */
        if (!eventRepository.existsById(eventId)) {
            throw new EventNotFoundException("Event not found with ID: " + eventId);
        }

        List<EventSession> sessions = eventSessionRepository.findByEventEventId(eventId);
        return eventSessionMapper.toResponseDTOList(sessions);
    }

    @Override
    public List<EventSessionResponseDTO> getEventSessionsByDate(Long eventId, LocalDate date) {
        /* verify event exists */
        if (!eventRepository.existsById(eventId)) {
            throw new EventNotFoundException("Event not found with ID: " + eventId);
        }

        List<EventSession> sessions = eventSessionRepository.findByEventEventIdAndSessionDate(eventId, date);
        return eventSessionMapper.toResponseDTOList(sessions);
    }

    @Override
    public EventSessionResponseDTO getSessionById(Long sessionId) {
        EventSession session = eventSessionRepository.findById(sessionId)
            .orElseThrow(() -> new EventSessionNotFoundException("Session not found with ID: " + sessionId));

        return eventSessionMapper.toResponseDTO(session);
    }

    @Override
    @Transactional
    public EventSessionResponseDTO updateSessionStatus(Long sessionId, String status) {
        EventSession session = eventSessionRepository.findById(sessionId)
            .orElseThrow(() -> new EventSessionNotFoundException("Session not found with ID: " + sessionId));

        System.out.println("Updating session ID " + sessionId + " to status: " + status); // debug log
        
        try {
            SessionStatus newStatus = SessionStatus.valueOf(status.toUpperCase());
            session.setSessionStatus(newStatus);
            eventSessionRepository.save(session);
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Invalid session status: " + status);
        }
        return eventSessionMapper.toResponseDTO(session);
    }

    @Override
    public String getQRTokenForSession(Long sessionId) {
        /* verify session exists and return stored QR token */
        EventSession session = eventSessionRepository.findById(sessionId)
            .orElseThrow(() -> new EventSessionNotFoundException("Session not found with ID: " + sessionId));

        return session.getQrTokenCode();
    }

    /* retrieve QR token for student - generates token with student id from JWT */
    public String getQRTokenForStudentCheckIn(Long sessionId, String jwtToken) {
        /* verify session exists */
        EventSession session = eventSessionRepository.findById(sessionId)
            .orElseThrow(() -> new EventSessionNotFoundException("Session not found with ID: " + sessionId));

        /* extract student id from JWT token */
        String studentId = jwtService.getStudentIdFromToken(jwtToken);
        if (studentId == null) {
            throw new InvalidQRTokenException("Unable to extract student ID from authentication token");
        }

        /* verify student is participant of this event */
        Long eventId = session.getEvent().getEventId();
        eventParticipantRepository.findByEventEventIdAndStudent_StudentId(eventId, studentId)
            .orElseThrow(() -> new StudentNotParticipantException(
                "Student " + studentId + " is not registered for this event"));

        /* generate QR token with student id embedded */
        return qrTokenService.generateQRToken(sessionId, jwtToken);
    }

    @Override
    public boolean isSessionActive(Long sessionId) {
        EventSession session = eventSessionRepository.findById(sessionId)
            .orElseThrow(() -> new EventSessionNotFoundException("Session not found with ID: " + sessionId));

        /* check if session is in ACTIVE status */
        if (session.getSessionStatus() != SessionStatus.ACTIVE) {
            return false;
        }

        /* check if current time is within session start and end time */
        LocalDateTime now = LocalDateTime.now();
        LocalTime currentTime = now.toLocalTime();
        LocalDate currentDate = now.toLocalDate();

        boolean isCurrentDate = currentDate.equals(session.getSessionDate());
        boolean isWithinTime = currentTime.isAfter(session.getStartTime()) && 
                              currentTime.isBefore(session.getEndTime());

        return isCurrentDate && isWithinTime;
    }
}
