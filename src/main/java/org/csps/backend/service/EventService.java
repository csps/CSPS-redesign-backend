package org.csps.backend.service;

import java.time.LocalDate;
import java.util.List;

import org.csps.backend.domain.dtos.request.EventPostRequestDTO;
import org.csps.backend.domain.dtos.request.EventUpdateRequestDTO;
import org.csps.backend.domain.dtos.response.EventResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

public interface EventService {
    EventResponseDTO postEvent(EventPostRequestDTO eventPostRequestDTO, MultipartFile eventImage) throws Exception;
    List<EventResponseDTO> getAllEvents();
    EventResponseDTO getEventById(Long eventId);
    EventResponseDTO getEventByS3ImageKey(String s3ImageKey);
    EventResponseDTO deleteEvent(Long eventId);
    EventResponseDTO putEvent(Long eventId, EventUpdateRequestDTO eventPostRequestDTO, MultipartFile eventImage) throws Exception;
    EventResponseDTO patchEvent(Long eventId, EventUpdateRequestDTO eventPostRequestDTO, MultipartFile eventImage) throws Exception;
    List<EventResponseDTO> getEventByDate(LocalDate eventDate);
    List<EventResponseDTO> getUpcomingEvents();
    Page<EventResponseDTO> getUpcomingEventsPaginated(Pageable pageable);
    
    List<EventResponseDTO> getEventsByMonth(int year, int month);
    
    List<EventResponseDTO> getPastEvents();

    /* Get Events By Student ID */
    Page<EventResponseDTO> getEventsByStudentId(Pageable pageable, String studentId);

    /* Search Events by name, date range, and location */
    Page<EventResponseDTO> searchEvent(String query, LocalDate startDate, LocalDate endDate, Pageable pageable);
}
