package org.csps.backend.repository;

import java.util.List;
import java.util.Optional;

import org.csps.backend.domain.entities.EventParticipant;
import org.csps.backend.domain.enums.ParticipationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventParticipantRepository extends JpaRepository<EventParticipant, Long> {
    
    /* check if a student has already joined an event */
    Optional<EventParticipant> findByEventEventIdAndStudent_StudentId(Long eventId, String studentId);
    
    /* get all participants for an event */
    List<EventParticipant> findByEventEventId(Long eventId);
    
    /* get all events a student has joined */
    List<EventParticipant> findByStudent_StudentId(String studentId);
    
    /* get only accepted participants for an event */
    List<EventParticipant> findByEventEventIdAndParticipationStatus(Long eventId, ParticipationStatus status);
    
    /* count total participants for an event */
    long countByEventEventId(Long eventId);
}
