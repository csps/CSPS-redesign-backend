package org.csps.backend.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.csps.backend.domain.dtos.request.AttendanceRecordSearchDTO;
import org.csps.backend.domain.dtos.response.AttendanceRecordResponseDTO;
import org.csps.backend.domain.entities.AttendanceRecord;
import org.csps.backend.domain.entities.EventParticipant;
import org.csps.backend.domain.entities.EventSession;
import org.csps.backend.domain.enums.SessionStatus;
import org.csps.backend.exception.DuplicateCheckInException;
import org.csps.backend.exception.EventSessionNotFoundException;
import org.csps.backend.exception.InvalidQRTokenException;
import org.csps.backend.exception.ParticipantNotFoundException;
import org.csps.backend.exception.SessionNotActiveException;
import org.csps.backend.exception.StudentNotParticipantException;
import org.csps.backend.mapper.AttendanceRecordMapper;
import org.csps.backend.repository.AttendanceRecordRepository;
import org.csps.backend.repository.EventParticipantRepository;
import org.csps.backend.repository.EventSessionRepository;
import org.csps.backend.repository.specification.AttendanceRecordSpecification;
import org.csps.backend.service.AttendanceRecordService;
import org.csps.backend.service.QRTokenService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AttendanceRecordServiceImpl implements AttendanceRecordService {

    private final AttendanceRecordRepository attendanceRecordRepository;
    private final EventSessionRepository eventSessionRepository;
    private final EventParticipantRepository eventParticipantRepository;
    private final AttendanceRecordMapper attendanceRecordMapper;
    private final QRTokenService qrTokenService;

    @Override
    @Transactional
    public AttendanceRecordResponseDTO checkInWithQR(Long sessionId, String qrToken) {
        /* 1. validate QR token signature and extract claims */
        if (!qrTokenService.isQRTokenValid(qrToken)) {
            throw new InvalidQRTokenException("QR code is invalid or has been tampered with");
        }

        /* 2. check if QR token is expired */
        if (qrTokenService.isQRTokenExpired(qrToken)) {
            throw new InvalidQRTokenException("QR code is expired, please scan a new one or ask for assistance");
        }

        /* 3. extract student id from QR token */
        String studentId = qrTokenService.extractStudentId(qrToken);
        if (studentId == null) {
            throw new InvalidQRTokenException("QR code does not contain valid student information");
        }

        /* 4. get and verify session exists */
        EventSession session = eventSessionRepository.findById(sessionId)
            .orElseThrow(() -> new EventSessionNotFoundException("Session not found with ID: " + sessionId));

        /* 5. verify session is ACTIVE (matches current time and date) */
        if (session.getSessionStatus() != SessionStatus.ACTIVE) {
            throw new SessionNotActiveException("Session is not currently active. Status: " + session.getSessionStatus());
        }

        /* 6. verify student is a participant in this event */
        EventParticipant participant = eventParticipantRepository.findByEventEventIdAndStudent_StudentId(
            session.getEvent().getEventId(), 
            studentId)
            .orElseThrow(() -> new StudentNotParticipantException("Student " + studentId + " is not registered for this event"));

        /* 7. check no duplicate check-in for this session */
        if (attendanceRecordRepository.existsByEventParticipantAndEventSession(participant, session)) {
            throw new DuplicateCheckInException("Already checked in for this session. One check-in per session please.");
        }

        /* 8. create attendance record */
        AttendanceRecord record = AttendanceRecord.builder()
            .eventParticipant(participant)
            .eventSession(session)
            .checkedInAt(LocalDateTime.now())
            .qrTokenUsed(qrToken)
            .build();

        AttendanceRecord savedRecord = attendanceRecordRepository.save(record);
        return attendanceRecordMapper.toResponseDTO(savedRecord);
    }

    @Override
    public Page<AttendanceRecordResponseDTO> getSessionAttendance(Long sessionId, Pageable pageable) {
        /* verify session exists */
        if (!eventSessionRepository.existsById(sessionId)) {
            throw new EventSessionNotFoundException("Session not found with ID: " + sessionId);
        }

        Page<AttendanceRecord> records = attendanceRecordRepository.findByEventSessionSessionId(sessionId, pageable);
        return records.map(attendanceRecordMapper::toResponseDTO);
    }

    @Override
    public List<AttendanceRecordResponseDTO> getParticipantAttendance(Long participantId) {
        /* verify participant exists */
        if (!eventParticipantRepository.existsById(participantId)) {
            throw new ParticipantNotFoundException("Participant not found with ID: " + participantId);
        }

        List<AttendanceRecord> records = attendanceRecordRepository.findByEventParticipantParticipantId(participantId);
        return attendanceRecordMapper.toResponseDTOList(records);
    }

    @Override
    public List<AttendanceRecordResponseDTO> getStudentEventAttendance(String studentId, Long eventId) {
        List<AttendanceRecord> records = attendanceRecordRepository.findByStudentAndEvent(studentId, eventId);
        return attendanceRecordMapper.toResponseDTOList(records);
    }

    @Override
    public long getSessionAttendanceCount(Long sessionId) {
        return attendanceRecordRepository.countByEventSessionSessionId(sessionId);
    }

    @Override
    public Page<AttendanceRecordResponseDTO> searchAttendanceRecords(AttendanceRecordSearchDTO searchDTO, Pageable pageable) {
        if (searchDTO == null) {
            searchDTO = new AttendanceRecordSearchDTO();
        }
        
        Specification<AttendanceRecord> spec = AttendanceRecordSpecification.withFilters(searchDTO);
        Page<AttendanceRecord> records = attendanceRecordRepository.findAll(spec, pageable);
        return records.map(attendanceRecordMapper::toResponseDTO);
    }

    @Override
    public Page<AttendanceRecordResponseDTO> getEventAttendance(Long eventId, Pageable pageable) {
        /* verify event exists */
        if (!eventSessionRepository.findByEventEventId(eventId).isEmpty()) {
            Page<AttendanceRecord> records = attendanceRecordRepository.findByEventId(eventId, pageable);
            return records.map(attendanceRecordMapper::toResponseDTO);
        }
        throw new EventSessionNotFoundException("No sessions found for event with ID: " + eventId);
    }
}