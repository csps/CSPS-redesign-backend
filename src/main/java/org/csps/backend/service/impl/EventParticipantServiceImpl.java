package org.csps.backend.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.csps.backend.domain.dtos.response.EventParticipantResponseDTO;
import org.csps.backend.domain.entities.Event;
import org.csps.backend.domain.entities.EventParticipant;
import org.csps.backend.domain.entities.Student;
import org.csps.backend.domain.enums.ParticipationStatus;
import org.csps.backend.exception.EventNotFoundException;
import org.csps.backend.exception.InvalidRequestException;
import org.csps.backend.exception.ParticipantAlreadyExistsException;
import org.csps.backend.exception.ParticipantNotFoundException;
import org.csps.backend.exception.StudentNotFoundException;
import org.csps.backend.mapper.EventParticipantMapper;
import org.csps.backend.repository.AttendanceRecordRepository;
import org.csps.backend.repository.EventParticipantRepository;
import org.csps.backend.repository.EventRepository;
import org.csps.backend.repository.StudentRepository;
import org.csps.backend.service.EventParticipantService;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EventParticipantServiceImpl implements EventParticipantService {

    private final EventParticipantRepository eventParticipantRepository;
    private final EventRepository eventRepository;
    private final StudentRepository studentRepository;
    private final EventParticipantMapper eventParticipantMapper;
    private final AttendanceRecordRepository attendanceRecordRepository;

    @Override
    @Transactional
    public EventParticipantResponseDTO joinEvent(String studentId, Long eventId) {
        /* verify student exists */
        Student student = studentRepository.findById(studentId)
            .orElseThrow(() -> new StudentNotFoundException("Student not found with ID: " + studentId));

        /* verify event exists */
        Event event = eventRepository.findById(eventId)
            .orElseThrow(() -> new EventNotFoundException("Event not found with ID: " + eventId));

        /* check if student already joined event */
        if (eventParticipantRepository.findByEventEventIdAndStudent_StudentId(eventId, studentId).isPresent()) {
            throw new ParticipantAlreadyExistsException("Student is already a participant of this event");
        }

        /* create and save new participant */
        EventParticipant participant = EventParticipant.builder()
            .event(event)
            .student(student)
            .participationStatus(ParticipationStatus.JOINED)
            .joinedDate(LocalDateTime.now())
            .build();

        EventParticipant savedParticipant = eventParticipantRepository.save(participant);
        return eventParticipantMapper.toResponseDTO(savedParticipant);
    }

    @Override
    @Transactional
    public void leaveEvent(String studentId, Long eventId) {
        /* find participant record */
        EventParticipant participant = eventParticipantRepository.findByEventEventIdAndStudent_StudentId(eventId, studentId)
            .orElseThrow(() -> new ParticipantNotFoundException("Student is not a participant of this event"));

        /* check if student has already attended any session; if yes, prevent from leaving */
        var attendanceRecords = attendanceRecordRepository.findByStudentAndEvent(studentId, eventId);
        if (!attendanceRecords.isEmpty()) {
            throw new InvalidRequestException("Cannot leave event after attending a session");
        }

        /* delete participant record */
        eventParticipantRepository.delete(participant);
    }

    @Override
    public List<EventParticipantResponseDTO> getEventParticipants(Long eventId) {
        /* verify event exists */
        if (!eventRepository.existsById(eventId)) {
            throw new EventNotFoundException("Event not found with ID: " + eventId);
        }

        /* get all participants for event */
        List<EventParticipant> participants = eventParticipantRepository.findByEventEventId(eventId);
        return eventParticipantMapper.toResponseDTOList(participants);
    }

    @Override
    public List<EventParticipantResponseDTO> getStudentJoinedEvents(String studentId) {
        /* verify student exists */
        if (!studentRepository.existsById(studentId)) {
            throw new StudentNotFoundException("Student not found with ID: " + studentId);
        }

        /* get all events student has joined */
        List<EventParticipant> participants = eventParticipantRepository.findByStudent_StudentId(studentId);
        return eventParticipantMapper.toResponseDTOList(participants);
    }

    @Override
    public boolean isStudentJoinedEvent(String studentId, Long eventId) {
        return eventParticipantRepository.findByEventEventIdAndStudent_StudentId(eventId, studentId).isPresent();
    }

    @Override
    @Transactional
    public void removeParticipant(Long participantId) {
        /* find participant */
        EventParticipant participant = eventParticipantRepository.findById(participantId)
            .orElseThrow(() -> new ParticipantNotFoundException("Participant not found with ID: " + participantId));

        /* delete participant */
        eventParticipantRepository.delete(participant);
    }

    @Override
    public long getParticipantCount(Long eventId) {
        return eventParticipantRepository.countByEventEventId(eventId);
    }
}
