package org.csps.backend.repository.specification;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.csps.backend.domain.dtos.request.AttendanceRecordSearchDTO;
import org.csps.backend.domain.entities.AttendanceRecord;
import org.csps.backend.domain.entities.EventParticipant;
import org.csps.backend.domain.entities.EventSession;
import org.csps.backend.domain.entities.Student;
import org.csps.backend.domain.entities.UserAccount;
import org.csps.backend.domain.entities.UserProfile;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;

public class AttendanceRecordSpecification {
    
    /**
     * build a dynamic specification for attendance record filtering
     * applies predicates based on non-null search criteria in attendancerecordsearchdto
     * supports filtering by student id, student name, session id, session name, event id, and date range
     */
    public static Specification<AttendanceRecord> withFilters(AttendanceRecordSearchDTO searchDTO) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            if (Objects.nonNull(searchDTO)) {
                // join with event participant entity
                Join<AttendanceRecord, EventParticipant> participantJoin = root.join("eventParticipant", JoinType.LEFT);
                
                // join with student entity
                Join<EventParticipant, Student> studentJoin = participantJoin.join("student", JoinType.LEFT);
                
                // join with useraccount entity
                Join<Student, UserAccount> userAccountJoin = studentJoin.join("userAccount", JoinType.LEFT);
                
                // join with userprofile entity
                Join<UserAccount, UserProfile> userProfileJoin = userAccountJoin.join("userProfile", JoinType.LEFT);
                
                // join with event session entity
                Join<AttendanceRecord, EventSession> sessionJoin = root.join("eventSession", JoinType.LEFT);
                
                // filter by student id
                if (Objects.nonNull(searchDTO.getStudentId()) && !searchDTO.getStudentId().isEmpty() && searchDTO.getStudentId() != null) {
                    predicates.add(cb.like(studentJoin.get("studentId").as(String.class), "%" + searchDTO.getStudentId().trim() + "%"));
                }
                
                // filter by student name (first name or last name)
                if (Objects.nonNull(searchDTO.getStudentName()) && !searchDTO.getStudentName().isEmpty()) {
                    String nameLike = "%" + searchDTO.getStudentName().toLowerCase().trim() + "%";
                    predicates.add(
                        cb.or(
                            cb.like(cb.lower(userProfileJoin.get("firstName")), nameLike),
                            cb.like(cb.lower(userProfileJoin.get("lastName")), nameLike)
                        )
                    );
                }
                
                // filter by session id
                if (Objects.nonNull(searchDTO.getSessionId())) {
                    predicates.add(cb.equal(sessionJoin.get("sessionId"), searchDTO.getSessionId()));
                }
                
                // filter by session name
                if (Objects.nonNull(searchDTO.getSessionName()) && !searchDTO.getSessionName().isEmpty()) {
                    String sessionNameLike = "%" + searchDTO.getSessionName().toLowerCase() + "%";
                    predicates.add(cb.like(cb.lower(sessionJoin.get("sessionName")), sessionNameLike));
                }
                
                // filter by event id
                if (Objects.nonNull(searchDTO.getEventId())) {
                    predicates.add(cb.equal(sessionJoin.get("event").get("eventId"), searchDTO.getEventId()));
                }
                
                // filter by start date
                if (Objects.nonNull(searchDTO.getStartDate())) {
                    predicates.add(cb.greaterThanOrEqualTo(root.get("checkedInAt"), searchDTO.getStartDate()));
                }
                
                // filter by end date
                if (Objects.nonNull(searchDTO.getEndDate())) {
                    predicates.add(cb.lessThanOrEqualTo(root.get("checkedInAt"), searchDTO.getEndDate()));
                }
            }
            
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
