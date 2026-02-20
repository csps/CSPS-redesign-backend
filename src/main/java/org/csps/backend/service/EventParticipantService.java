package org.csps.backend.service;

import java.util.List;

import org.csps.backend.domain.dtos.response.EventParticipantResponseDTO;

public interface EventParticipantService {
    
    /* student joins an event - auto-accepted */
    EventParticipantResponseDTO joinEvent(String studentId, Long eventId);
    
    /* student leaves an event */
    void leaveEvent(String studentId, Long eventId);
    
    /* get all participants for an event */
    List<EventParticipantResponseDTO> getEventParticipants(Long eventId);
    
    /* get events a student has joined */
    List<EventParticipantResponseDTO> getStudentJoinedEvents(String studentId);
    
    /* check if student is already joined to event */
    boolean isStudentJoinedEvent(String studentId, Long eventId);
    
    /* admin removes a participant from event */
    void removeParticipant(Long participantId);
    
    /* get total participant count for an event */
    long getParticipantCount(Long eventId);
}
