package org.csps.backend.repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.csps.backend.domain.entities.Event;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findByEventDate(LocalDate eventDate);
    Event findByS3ImageKey(String s3ImageKey);
    
    @Query("""
            SELECT e FROM Event e
            WHERE e.eventDate >= :today
            ORDER BY e.eventDate ASC, e.startTime ASC
        """)
    List<Event> findUpcomingEvents(@Param("today") LocalDate today);
    
    @Query("""
            SELECT e FROM Event e
            WHERE YEAR(e.eventDate) = :year AND MONTH(e.eventDate) = :month
            ORDER BY e.eventDate ASC, e.startTime ASC
        """)
    List<Event> findEventsByMonth(@Param("year") int year, @Param("month") int month);
    
    @Query("""
            SELECT e FROM Event e
            WHERE e.eventDate < :today
            ORDER BY e.eventDate DESC, e.startTime DESC
        """)
    List<Event> findPastEvents(@Param("today") LocalDate today);
    
    @Query("""
            SELECT CASE WHEN COUNT(e) > 0 THEN TRUE ELSE FALSE END
            FROM Event e
            WHERE e.eventDate = :eventDate
            AND e.startTime < :endTime
            AND e.endTime > :startTime
        """)
        boolean isDateOverlap(
            @Param("eventDate") LocalDate eventDate,
            @Param("startTime") LocalTime startTime,
            @Param("endTime") LocalTime endTime
        );

    Page<Event> findByParticipants_Student_StudentId(Pageable pageable, String studentId);
}
