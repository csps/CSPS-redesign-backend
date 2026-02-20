package org.csps.backend.repository;

import java.util.List;
import java.util.Optional;

import org.csps.backend.domain.entities.AttendanceRecord;
import org.csps.backend.domain.entities.EventParticipant;
import org.csps.backend.domain.entities.EventSession;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AttendanceRecordRepository extends JpaRepository<AttendanceRecord, Long>, JpaSpecificationExecutor<AttendanceRecord> {
    
    /* check if participant already checked in for a session */
    Optional<AttendanceRecord> findByEventParticipantAndEventSession(EventParticipant participant, EventSession session);
    
    /* get all attendance records for a session */
    List<AttendanceRecord> findByEventSessionSessionId(Long sessionId);
    
    /* get all attendance records for a session with pagination */
    Page<AttendanceRecord> findByEventSessionSessionId(Long sessionId, Pageable pageable);
    
    /* get all attendance records for a participant */
    List<AttendanceRecord> findByEventParticipantParticipantId(Long participantId);
    
    @Query("SELECT a FROM AttendanceRecord a " +
       "WHERE a.eventParticipant.student.studentId = :studentId " +
       "AND a.eventSession.event.eventId = :eventId")
    List<AttendanceRecord> findByStudentAndEvent(
        @Param("studentId") String studentId, 
        @Param("eventId") Long eventId
    );
    
    /* get all attendance records for an event with pagination */
    @Query("SELECT a FROM AttendanceRecord a " +
       "WHERE a.eventSession.event.eventId = :eventId")
    Page<AttendanceRecord> findByEventId(
        @Param("eventId") Long eventId,
        Pageable pageable
    );

    /* check if attendance exists */
    boolean existsByEventParticipantAndEventSession(EventParticipant participant, EventSession session);
    
    /* count attendance for a session */
    long countByEventSessionSessionId(Long sessionId);
}
