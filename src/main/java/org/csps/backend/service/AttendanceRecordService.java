package org.csps.backend.service;

import java.util.List;

import org.csps.backend.domain.dtos.request.AttendanceRecordSearchDTO;
import org.csps.backend.domain.dtos.response.AttendanceRecordResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AttendanceRecordService {
    
    /* check-in student to a session using QR token */
    AttendanceRecordResponseDTO checkInWithQR(Long sessionId, String qrToken);
    
    /* get all attendance records for a session with pagination, page size of 6 per page */
    Page<AttendanceRecordResponseDTO> getSessionAttendance(Long sessionId, Pageable pageable);
    
    /* get all attendance records for a participant */
    List<AttendanceRecordResponseDTO> getParticipantAttendance(Long participantId);
    
    /* get attendance records for a student in a specific event */
    List<AttendanceRecordResponseDTO> getStudentEventAttendance(String studentId, Long eventId);
    
    /* get attendance count for a session */
    long getSessionAttendanceCount(Long sessionId);
    
    /* search and filter attendance records with pagination, page size of 6 per page */
    Page<AttendanceRecordResponseDTO> searchAttendanceRecords(AttendanceRecordSearchDTO searchDTO, Pageable pageable);
    
    /* get all attendance records for an event with pagination, page size of 6 per page */
    Page<AttendanceRecordResponseDTO> getEventAttendance(Long eventId, Pageable pageable);
}
