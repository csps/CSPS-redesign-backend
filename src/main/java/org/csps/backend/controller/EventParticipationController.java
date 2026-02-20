package org.csps.backend.controller;

import java.util.List;

import org.csps.backend.annotation.Auditable;
import org.csps.backend.domain.dtos.response.EventParticipantResponseDTO;
import org.csps.backend.domain.dtos.response.GlobalResponseBuilder;
import org.csps.backend.domain.enums.AuditAction;
import org.csps.backend.service.EventParticipantService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/event")
public class EventParticipationController {

    private final EventParticipantService eventParticipantService;

    /* student joins an event */
    @PostMapping("/{eventId}/join")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<GlobalResponseBuilder<EventParticipantResponseDTO>> joinEvent(
            @PathVariable Long eventId,
            @AuthenticationPrincipal String studentId) {
        EventParticipantResponseDTO participant = eventParticipantService.joinEvent(studentId, eventId);
        String message = "Successfully joined the event";
        return GlobalResponseBuilder.buildResponse(message, participant, HttpStatus.CREATED);
    }

    /* student leaves an event */
    @DeleteMapping("/{eventId}/leave")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<GlobalResponseBuilder<Void>> leaveEvent(
            @PathVariable Long eventId,
            @AuthenticationPrincipal String studentId) {
        eventParticipantService.leaveEvent(studentId, eventId);
        String message = "Successfully left the event";
        return GlobalResponseBuilder.buildResponse(message, null, HttpStatus.OK);
    }

    /* get student's joined events */
    @GetMapping("/my-joined")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<GlobalResponseBuilder<List<EventParticipantResponseDTO>>> getMyJoinedEvents(
            @AuthenticationPrincipal String studentId) {
        List<EventParticipantResponseDTO> events = eventParticipantService.getStudentJoinedEvents(studentId);
        String message = "User's joined events retrieved successfully";
        return GlobalResponseBuilder.buildResponse(message, events, HttpStatus.OK);
    }

    /* admin: get all participants for an event */
    @GetMapping("/{eventId}/participants")
    @PreAuthorize("hasRole('ADMIN_EXECUTIVE')")
    public ResponseEntity<GlobalResponseBuilder<List<EventParticipantResponseDTO>>> getEventParticipants(
            @PathVariable Long eventId) {
        List<EventParticipantResponseDTO> participants = eventParticipantService.getEventParticipants(eventId);
        String message = "Event participants retrieved successfully";
        return GlobalResponseBuilder.buildResponse(message, participants, HttpStatus.OK);
    }

    /* admin: remove a participant from event */
    @DeleteMapping("/participant/{participantId}")
    @PreAuthorize("hasRole('ADMIN_EXECUTIVE')")
    @Auditable(action = AuditAction.DELETE, resourceType = "EventParticipant")
    public ResponseEntity<GlobalResponseBuilder<Void>> removeParticipant(
            @PathVariable Long participantId) {
        eventParticipantService.removeParticipant(participantId);
        String message = "Participant removed successfully";
        return GlobalResponseBuilder.buildResponse(message, null, HttpStatus.OK);
    }
}
