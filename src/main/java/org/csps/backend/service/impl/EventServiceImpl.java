package org.csps.backend.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import org.csps.backend.domain.dtos.request.EventPostRequestDTO;
import org.csps.backend.domain.dtos.request.EventUpdateRequestDTO;
import org.csps.backend.domain.dtos.response.EventResponseDTO;
import org.csps.backend.domain.entities.Event;
import org.csps.backend.domain.enums.EventStatus;
import org.csps.backend.domain.enums.EventType;
import org.csps.backend.exception.EventNotFoundException;
import org.csps.backend.exception.InvalidRequestException;
import org.csps.backend.mapper.EventMapper;
import org.csps.backend.repository.EventRepository;
import org.csps.backend.service.EventService;
import org.csps.backend.service.S3Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final EventMapper eventMapper;
    private final S3Service s3Service;

    @Override
    @Transactional
    public EventResponseDTO postEvent(EventPostRequestDTO eventPostRequestDTO, MultipartFile eventImage) throws Exception {
        // convert the request to entity
        Event event = eventMapper.toEntity(eventPostRequestDTO);

        // check if the event already exists
        boolean existsOverlap = eventRepository.isDateOverlap(
            event.getEventDate(),
            event.getStartTime(),
            event.getEndTime()
        );

        if (existsOverlap) {
            throw new InvalidRequestException("Event already exists with the same date and time");
        }

        // validations
        if (eventPostRequestDTO.getEventName() == null || eventPostRequestDTO.getEventName().trim().isEmpty() ||
            eventPostRequestDTO.getEventDescription() == null || eventPostRequestDTO.getEventDescription().trim().isEmpty() ||
            eventPostRequestDTO.getEventLocation() == null || eventPostRequestDTO.getEventLocation().trim().isEmpty() ||
            eventPostRequestDTO.getEventDate() == null || eventPostRequestDTO.getStartTime() == null || eventPostRequestDTO.getEndTime() == null ||
            eventPostRequestDTO.getEventType() == null || eventPostRequestDTO.getEventStatus() == null) {
            throw new InvalidRequestException("Invalid Credential");
        }

        // validate the date (must be present or future)
        if (eventPostRequestDTO.getEventDate().isBefore(LocalDate.now())) {
            throw new InvalidRequestException("Event date cannot be in the past");
        }

        // if event is for today, validate that start time hasn't passed
        if (eventPostRequestDTO.getEventDate().equals(LocalDate.now()) && 
            eventPostRequestDTO.getStartTime().isBefore(LocalTime.now())) {
            throw new InvalidRequestException("Event start time cannot be in the past for today's date");
        }

        // validate the time range
        if (eventPostRequestDTO.getStartTime().isAfter(eventPostRequestDTO.getEndTime()) || eventPostRequestDTO.getStartTime().equals(eventPostRequestDTO.getEndTime())) {
            throw new InvalidRequestException("Invalid Time Range");
        }

        event.setCreatedAt(LocalDateTime.now());
        event.setUpdatedAt(LocalDateTime.now());
        event.setS3ImageKey("placeholder");
        
        LocalTime startTime = eventPostRequestDTO.getStartTime();
        LocalTime endTime = eventPostRequestDTO.getEndTime();

        if (startTime.isAfter(endTime) || startTime.equals(endTime)) {
            throw new InvalidRequestException("Invalid Time Range");
        }

        // persist the entity first
        Event savedEvent = eventRepository.save(event);

        // upload image to S3 if provided
        if (eventImage != null && !eventImage.isEmpty()) {
            String s3ImageKey = s3Service.uploadFile(eventImage, savedEvent.getEventId(), "event");
            savedEvent.setS3ImageKey(s3ImageKey);
            eventRepository.save(savedEvent);
        }

        // convert the entity into response dto
        EventResponseDTO eventResponseDTO = eventMapper.toResponseDTO(savedEvent);

        return eventResponseDTO;
    }

    @Override
    public List<EventResponseDTO> getAllEvents() {

        // convert all the event entities to event response dto
        List<EventResponseDTO> events = eventRepository.findAll()
                                    .stream()
                                    .map(eventMapper::toResponseDTO)
                                    .toList();

        return events;
    }

    @Override
    public EventResponseDTO getEventById(Long eventId) {

        // find the event by id
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new EventNotFoundException("Event not found with id: " + eventId));

        // convert the entity into response dto
        EventResponseDTO eventResponseDTO = eventMapper.toResponseDTO(event);

        return eventResponseDTO;
    }

    @Override
    @Transactional
    public EventResponseDTO deleteEvent(Long eventId) {

        // find the event by id
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new EventNotFoundException("Event not found with id: " + eventId));

        // delete the event
        eventRepository.delete(event);

        // convert the entity into response dto
        EventResponseDTO eventResponseDTO = eventMapper.toResponseDTO(event);

        return eventResponseDTO;
    }

    @Override
    @Transactional
    public EventResponseDTO putEvent(Long eventId, EventUpdateRequestDTO eventUpdateRequestDTO, MultipartFile eventImage) throws Exception {
    
        // find the event by id
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new EventNotFoundException("Event not found with id: " + eventId));

        // get the new values
        String newEventName = eventUpdateRequestDTO.getEventName();
        String newEventDescription = eventUpdateRequestDTO.getEventDescription();
        String newEventLocation = eventUpdateRequestDTO.getEventLocation();
        LocalDate newEventDate = eventUpdateRequestDTO.getEventDate();
        LocalTime newEventStartTime = eventUpdateRequestDTO.getStartTime();
        LocalTime newEventEndTime = eventUpdateRequestDTO.getEndTime();
        EventType newEventType = eventUpdateRequestDTO.getEventType();
        EventStatus newEventStatus = eventUpdateRequestDTO.getEventStatus();

        // validate the new values
        if (newEventName == null || newEventName.trim().isEmpty() ||
            newEventDescription == null || newEventDescription.trim().isEmpty() ||
            newEventLocation == null || newEventLocation.trim().isEmpty() ||
            newEventDate == null || newEventStartTime == null || newEventEndTime == null ||
            newEventType == null || newEventStatus == null) {
            throw new InvalidRequestException("Invalid Credential");
        }

        // validate the date (must be present or future)
        if (newEventDate.isBefore(LocalDate.now())) {
            throw new InvalidRequestException("Event date cannot be in the past");
        }

        // if event is for today, validate that start time hasn't passed
        if (newEventDate.equals(LocalDate.now()) && newEventStartTime.isBefore(LocalTime.now())) {
            throw new InvalidRequestException("Event start time cannot be in the past for today's date");
        }

        // validate the time range
        if (newEventStartTime.isAfter(newEventEndTime) || newEventStartTime.equals(newEventEndTime)) {
            throw new InvalidRequestException("Invalid Time Range");
        }

        // set the new values
        event.setEventName(newEventName);
        event.setEventDescription(newEventDescription);
        event.setEventLocation(newEventLocation);
        event.setEventDate(newEventDate);
        event.setStartTime(newEventStartTime);
        event.setEndTime(newEventEndTime);
        event.setEventType(newEventType);
        event.setEventStatus(newEventStatus);

        event.setUpdatedAt(LocalDateTime.now());

        // check if the event already exists
        boolean existsOverlap = eventRepository.isDateOverlap(
            event.getEventDate(),
            event.getEndTime(),
            event.getStartTime()
        );

        if (existsOverlap) {
            throw new InvalidRequestException("Event already exists with the same date and time");
        }

        // handle image upload if provided
        if (eventImage != null && !eventImage.isEmpty()) {
            String s3ImageKey = s3Service.uploadFile(eventImage, event.getEventId(), "event");
            event.setS3ImageKey(s3ImageKey);
        }

        // save the event
        eventRepository.save(event);

        // convert the entity into response dto
        EventResponseDTO eventResponseDTO = eventMapper.toResponseDTO(event);

        // return the response dto
        return eventResponseDTO;
    }

    @Override
    @Transactional
    public EventResponseDTO patchEvent(Long eventId, EventUpdateRequestDTO eventUpdateRequestDTO, MultipartFile eventImage) throws Exception {
    
        // find the event by id
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new EventNotFoundException("Event not found with id: " + eventId));

        // get the new values
        String newEventName = eventUpdateRequestDTO.getEventName();
        String newEventDescription = eventUpdateRequestDTO.getEventDescription();
        String newEventLocation = eventUpdateRequestDTO.getEventLocation();
        LocalDate newEventDate = eventUpdateRequestDTO.getEventDate();
        LocalTime newEventStartTime = eventUpdateRequestDTO.getStartTime();
        LocalTime newEventEndTime = eventUpdateRequestDTO.getEndTime();
        EventType newEventType = eventUpdateRequestDTO.getEventType();
        EventStatus newEventStatus = eventUpdateRequestDTO.getEventStatus();

        // validate the date (must be present or future) - BEFORE setting values
        if (newEventDate != null && newEventDate.isBefore(LocalDate.now())) {
            throw new InvalidRequestException("Event date cannot be in the past");
        }

        // validate the time range - BEFORE setting values
        if ((newEventStartTime != null && newEventEndTime != null) && 
            (newEventStartTime.isAfter(newEventEndTime) || newEventStartTime.equals(newEventEndTime))) {
            throw new InvalidRequestException("Invalid Time Range");
        }

        // set the new values
        if (newEventName != null && !newEventName.trim().isEmpty()) {
            event.setEventName(newEventName);
        }
        if (newEventDescription != null && !newEventDescription.trim().isEmpty()) {
            event.setEventDescription(newEventDescription);
        }   
        if (newEventLocation != null && !newEventLocation.trim().isEmpty()) {
            event.setEventLocation(newEventLocation);
        }
        if (newEventDate != null) {
            event.setEventDate(newEventDate);
        }
        if (newEventType != null) {
            event.setEventType(newEventType);
        }
        if (newEventStatus != null) {
            event.setEventStatus(newEventStatus);
        }
        
        
        if (newEventStartTime != null) {
            event.setStartTime(newEventStartTime);
        }
        if (newEventEndTime != null) {
            event.setEndTime(newEventEndTime);
        }

        event.setUpdatedAt(LocalDateTime.now());

        // check if the event already exists
        boolean existsOverlap = eventRepository.isDateOverlap(
            event.getEventDate(),
            event.getEndTime(),
            event.getStartTime()
        );

        
        if (existsOverlap) {
            throw new InvalidRequestException("Event already exists with the same date and time");
        }

        // handle image upload if provided
        if (eventImage != null && !eventImage.isEmpty()) {
            String s3ImageKey = s3Service.uploadFile(eventImage, event.getEventId(), "event");
            event.setS3ImageKey(s3ImageKey);
        }

        // save the event
        eventRepository.save(event);

        // convert the entity into response dto
        EventResponseDTO eventResponseDTO = eventMapper.toResponseDTO(event);

        // return the response dto
        return eventResponseDTO;
    }

    @Override
    public List<EventResponseDTO> getEventByDate(LocalDate eventDate) {
        List<Event> events = eventRepository.findByEventDate(eventDate);
    


        List<EventResponseDTO> eventResponseDTOs = events.stream()
                .map(eventMapper::toResponseDTO)
                .toList();
                
        // return the response dto
        return eventResponseDTOs;
    }

    @Override
    public EventResponseDTO getEventByS3ImageKey(String s3ImageKey) {
        Event event = eventRepository.findByS3ImageKey(s3ImageKey);
    
        if (event == null) 
            throw new EventNotFoundException("Event not found with S3 Image Key: " + s3ImageKey);

        EventResponseDTO eventResponseDTO = eventMapper.toResponseDTO(event);
                
        // return the response dto
        return eventResponseDTO;
    }

    @Override
    public List<EventResponseDTO> getUpcomingEvents() {
        LocalDate today = LocalDate.now();
        List<Event> upcomingEvents = eventRepository.findUpcomingEvents(today);
        
        return upcomingEvents.stream()
                .map(eventMapper::toResponseDTO)
                .toList();
    }

    @Override
    public List<EventResponseDTO> getEventsByMonth(int year, int month) {
        if (month < 1 || month > 12)
            throw new InvalidRequestException("Invalid month. Month must be between 1 and 12");
            
        List<Event> events = eventRepository.findEventsByMonth(year, month);
    
        return events.stream()
                .map(eventMapper::toResponseDTO)
                .toList();
    }

    @Override
    public List<EventResponseDTO> getPastEvents() {
        LocalDate today = LocalDate.now();
        List<Event> pastEvents = eventRepository.findPastEvents(today);
        


        return pastEvents.stream()
                .map(eventMapper::toResponseDTO)
                .toList();
    }

    @Override
    public Page<EventResponseDTO> getEventsByStudentId(Pageable pageable, String studentId) {
        Page<Event> eventsPage = eventRepository.findByParticipants_Student_StudentId(pageable, studentId);
        return eventsPage.map(eventMapper::toResponseDTO);
    }

    @Override
    public Page<EventResponseDTO> getUpcomingEventsPaginated(Pageable pageable) {
        /* retrieve upcoming events with pagination; default size is managed by pageable parameter */
        LocalDate today = LocalDate.now();
        Page<Event> upcomingEventsPage = eventRepository.findUpcomingEventsPaginated(today, pageable);
        return upcomingEventsPage.map(eventMapper::toResponseDTO);
    }

    @Override
    public Page<EventResponseDTO> searchEvent(String query, LocalDate startDate, LocalDate endDate, Pageable pageable) {
        /* search events by name, location, or description within optional date range */
        if (query == null || query.trim().isEmpty()) {
            throw new InvalidRequestException("Search query cannot be empty");
        }
        
        Page<Event> searchResults = eventRepository.searchEvents(query.trim(), startDate, endDate, pageable);
        return searchResults.map(eventMapper::toResponseDTO);
    }
}
