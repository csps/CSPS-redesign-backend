package org.csps.backend.repository;

import java.util.List;
import java.util.Optional;

import org.csps.backend.domain.entities.EventParticipant;
import org.csps.backend.domain.enums.ParticipationStatus;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventParticipantRepository extends JpaRepository<EventParticipant, Long> {
    
    /* check if a student has already joined an event */
    @EntityGraph(attributePaths = {"event", "student", "student.userAccount", "student.userAccount.userProfile"}, type = EntityGraph.EntityGraphType.FETCH)
    Optional<EventParticipant> findByEventEventIdAndStudent_StudentId(Long eventId, String studentId);
    
    /* get all participants for an event with eager loading to prevent N+1 queries */
    @EntityGraph(attributePaths = {"event", "student", "student.userAccount", "student.userAccount.userProfile"}, type = EntityGraph.EntityGraphType.FETCH)
    List<EventParticipant> findByEventEventId(Long eventId);
    
    /* get all events a student has joined with eager loading */
    @EntityGraph(attributePaths = {"event", "student", "student.userAccount", "student.userAccount.userProfile"}, type = EntityGraph.EntityGraphType.FETCH)
    List<EventParticipant> findByStudent_StudentId(String studentId);
    
    /* get only accepted participants for an event with eager loading */
    @EntityGraph(attributePaths = {"event", "student", "student.userAccount", "student.userAccount.userProfile"}, type = EntityGraph.EntityGraphType.FETCH)
    List<EventParticipant> findByEventEventIdAndParticipationStatus(Long eventId, ParticipationStatus status);
    
    /* count total participants for an event */
    long countByEventEventId(Long eventId);
}
