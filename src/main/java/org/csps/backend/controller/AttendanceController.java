package org.csps.backend.controller;

import java.time.LocalDate;
import java.util.List;

import org.csps.backend.annotation.Auditable;
import org.csps.backend.domain.dtos.request.AttendanceRecordSearchDTO;
import org.csps.backend.domain.dtos.request.CheckInRequestDTO;
import org.csps.backend.domain.dtos.request.EventSessionRequestDTO;
import org.csps.backend.domain.dtos.response.AttendanceRecordResponseDTO;
import org.csps.backend.domain.dtos.response.EventSessionResponseDTO;
import org.csps.backend.domain.dtos.response.GlobalResponseBuilder;
import org.csps.backend.domain.enums.AuditAction;
import org.csps.backend.service.AttendanceRecordService;
import org.csps.backend.service.EventSessionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/event")
public class AttendanceController {

    private final EventSessionService eventSessionService;
    private final AttendanceRecordService attendanceRecordService;

    /* admin: create a session for an event */
    @PostMapping("/{eventId}/session")
    @PreAuthorize("hasRole('ADMIN_EXECUTIVE')")
    @Auditable(action = AuditAction.CREATE, resourceType = "EventSession")
    public ResponseEntity<GlobalResponseBuilder<EventSessionResponseDTO>> createSession(
            @PathVariable Long eventId,
            @Valid @RequestBody EventSessionRequestDTO sessionRequestDTO) {
        EventSessionResponseDTO session = eventSessionService.createSession(eventId, sessionRequestDTO);
        String message = "Event session created successfully";
        return GlobalResponseBuilder.buildResponse(message, session, HttpStatus.CREATED);
    }

    /* admin: get all sessions for an event */
    @GetMapping("/{eventId}/sessions")
    @PreAuthorize("hasRole('ADMIN_EXECUTIVE') or hasRole('STUDENT')")
    public ResponseEntity<GlobalResponseBuilder<List<EventSessionResponseDTO>>> getEventSessions(
            @PathVariable Long eventId) {
        List<EventSessionResponseDTO> sessions = eventSessionService.getEventSessions(eventId);
        String message = "Event sessions retrieved successfully";
        return GlobalResponseBuilder.buildResponse(message, sessions, HttpStatus.OK);
    }

    /* admin: get sessions for a specific date */
    @GetMapping("/{eventId}/sessions/by-date")
    @PreAuthorize("hasRole('ADMIN_EXECUTIVE') or hasRole('STUDENT')")
    public ResponseEntity<GlobalResponseBuilder<List<EventSessionResponseDTO>>> getSessionsByDate(
            @PathVariable Long eventId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<EventSessionResponseDTO> sessions = eventSessionService.getEventSessionsByDate(eventId, date);
        String message = "Sessions for " + date + " retrieved successfully";
        return GlobalResponseBuilder.buildResponse(message, sessions, HttpStatus.OK);
    }

    /* admin: update session status */
    @PutMapping("/session/{sessionId}/status")
    @PreAuthorize("hasRole('ADMIN_EXECUTIVE')")
    @Auditable(action = AuditAction.UPDATE, resourceType = "EventSession")
    public ResponseEntity<GlobalResponseBuilder<EventSessionResponseDTO>> updateSessionStatus(
            @PathVariable Long sessionId,
            @RequestParam String status) {
        EventSessionResponseDTO updatedSession = eventSessionService.updateSessionStatus(sessionId, status);
        String message = "Session status updated to " + status;
        return GlobalResponseBuilder.buildResponse(message, updatedSession, HttpStatus.OK);
    }

    /* student: retrieve QR token for a session */
    @GetMapping("/session/{sessionId}/qr-token")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<GlobalResponseBuilder<String>> getQRTokenForSession(
            @PathVariable Long sessionId,
            @org.springframework.web.bind.annotation.RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader) {
        /* extract JWT token from Authorization header (remove 'Bearer ' prefix) */
        String jwtToken = authHeader != null && authHeader.startsWith("Bearer ") ? authHeader.substring(7) : null;
        String qrToken = eventSessionService.getQRTokenForStudentCheckIn(sessionId, jwtToken);
        String message = "QR token retrieved successfully";
        return GlobalResponseBuilder.buildResponse(message, qrToken, HttpStatus.OK);
    }

    /* student: check-in to a session using QR code */
    @PostMapping("/session/{sessionId}/check-in")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<GlobalResponseBuilder<AttendanceRecordResponseDTO>> checkInToSession(
            @PathVariable Long sessionId,
            @Valid @RequestBody CheckInRequestDTO checkInRequest) {
        AttendanceRecordResponseDTO record = attendanceRecordService.checkInWithQR(
            sessionId,
            checkInRequest.getQrToken()
        );
        String message = "Checked in successfully to " + record.getSessionName();
        return GlobalResponseBuilder.buildResponse(message, record, HttpStatus.CREATED);
    }

    /* admin: view attendance for a session (who checked in) with pagination, page size of 6 per page */
    @GetMapping("/session/{sessionId}/attendance")
    @PreAuthorize("hasRole('ADMIN_EXECUTIVE')")
    public ResponseEntity<GlobalResponseBuilder<Page<AttendanceRecordResponseDTO>>> getSessionAttendance(
            @PathVariable Long sessionId,
            @PageableDefault(size = 6, sort = "checkedInAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<AttendanceRecordResponseDTO> records = attendanceRecordService.getSessionAttendance(sessionId, pageable);
        String message = "Attendance records for session retrieved successfully";
        return GlobalResponseBuilder.buildResponse(message, records, HttpStatus.OK);
    }

    /* admin: view attendance count for a session */
    @GetMapping("/session/{sessionId}/attendance/count")
    @PreAuthorize("hasRole('ADMIN_EXECUTIVE')")
    public ResponseEntity<GlobalResponseBuilder<Long>> getSessionAttendanceCount(
            @PathVariable Long sessionId) {
        long count = attendanceRecordService.getSessionAttendanceCount(sessionId);
        String message = "Attendance count for session retrieved successfully";
        return GlobalResponseBuilder.buildResponse(message, count, HttpStatus.OK);
    }

    /* student: view their attendance records for an event */
    @GetMapping("/{eventId}/my-attendance")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<GlobalResponseBuilder<List<AttendanceRecordResponseDTO>>> getMyEventAttendance(
            @PathVariable Long eventId,
            @AuthenticationPrincipal String studentId) {
        List<AttendanceRecordResponseDTO> records = attendanceRecordService.getStudentEventAttendance(studentId, eventId);
        String message = "Your attendance records retrieved successfully";
        return GlobalResponseBuilder.buildResponse(message, records, HttpStatus.OK);
    }

    /* admin: search and filter attendance records with pagination, page size of 6 per page */
    @GetMapping("/attendance/search")
    @PreAuthorize("hasRole('ADMIN_EXECUTIVE')")
    public ResponseEntity<GlobalResponseBuilder<Page<AttendanceRecordResponseDTO>>> searchAttendanceRecords(
            @ModelAttribute AttendanceRecordSearchDTO searchDTO,
            @PageableDefault(size = 6, sort = "checkedInAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<AttendanceRecordResponseDTO> results = attendanceRecordService.searchAttendanceRecords(searchDTO, pageable);
        String message = "Attendance records retrieved successfully";
        return GlobalResponseBuilder.buildResponse(message, results, HttpStatus.OK);
    }

    /* admin/student: get all attendance records for an event with pagination, page size of 6 per page */
    @GetMapping("/{eventId}/attendance")
    @PreAuthorize("hasRole('ADMIN_EXECUTIVE') or hasRole('STUDENT')")
    public ResponseEntity<GlobalResponseBuilder<Page<AttendanceRecordResponseDTO>>> getEventAttendance(
            @PathVariable Long eventId,
            @PageableDefault(size = 6, sort = "checkedInAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<AttendanceRecordResponseDTO> records = attendanceRecordService.getEventAttendance(eventId, pageable);
        String message = "Attendance records for event retrieved successfully";
        return GlobalResponseBuilder.buildResponse(message, records, HttpStatus.OK);
    }
}
