package org.csps.backend.repository.specification;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.csps.backend.domain.dtos.request.StudentMembershipSearchDTO;
import org.csps.backend.domain.entities.Student;
import org.csps.backend.domain.entities.StudentMembership;
import org.csps.backend.domain.entities.UserAccount;
import org.csps.backend.domain.entities.UserProfile;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;

/**
 * JPA Specification builder for StudentMembership search/filter.
 * Follows the same pattern as OrderSpecification.
 * Builds dynamic predicates based on non-null fields in StudentMembershipSearchDTO.
 *
 * Join path: StudentMembership → Student → UserAccount → UserProfile
 */
@Component
public class StudentMembershipSpecification {

    /**
     * Build a dynamic Specification for membership filtering.
     * Applies predicates based on non-null search criteria.
     * Supports filtering by student name, student ID,
     * active status, academic year start, and academic year end.
     *
     * @param searchDTO the search criteria DTO
     * @return composed Specification with all applicable filters
     */
    public static Specification<StudentMembership> withFilters(StudentMembershipSearchDTO searchDTO) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (Objects.nonNull(searchDTO)) {
                // Join: StudentMembership → Student
                Join<StudentMembership, Student> studentJoin = root.join("student", JoinType.LEFT);

                // Join: Student → UserAccount
                Join<Student, UserAccount> userAccountJoin = studentJoin.join("userAccount", JoinType.LEFT);

                // Join: UserAccount → UserProfile
                Join<UserAccount, UserProfile> userProfileJoin = userAccountJoin.join("userProfile", JoinType.LEFT);

                // Filter by student name (first name or last name, case-insensitive partial match)
                if (Objects.nonNull(searchDTO.getStudentName()) && !searchDTO.getStudentName().isEmpty()) {
                    String nameLike = "%" + searchDTO.getStudentName().toLowerCase() + "%";
                    predicates.add(
                        cb.or(
                            cb.like(cb.lower(userProfileJoin.get("firstName")), nameLike),
                            cb.like(cb.lower(userProfileJoin.get("lastName")), nameLike)
                        )
                    );
                }

                // Filter by exact student ID
                if (Objects.nonNull(searchDTO.getStudentId()) && !searchDTO.getStudentId().isEmpty()) {
                    predicates.add(cb.equal(studentJoin.get("studentId"), searchDTO.getStudentId()));
                }

                // Filter by active status ("ACTIVE" or "INACTIVE")
                if (Objects.nonNull(searchDTO.getActiveStatus()) && !searchDTO.getActiveStatus().isEmpty()) {
                    boolean isActive = "ACTIVE".equalsIgnoreCase(searchDTO.getActiveStatus());
                    predicates.add(cb.equal(root.get("active"), isActive));
                }

                // Filter by academic year start
                if (Objects.nonNull(searchDTO.getYearStart())) {
                    predicates.add(cb.equal(root.get("yearStart"), searchDTO.getYearStart()));
                }

                // Filter by academic year end
                if (Objects.nonNull(searchDTO.getYearEnd())) {
                    predicates.add(cb.equal(root.get("yearEnd"), searchDTO.getYearEnd()));
                }
            }

            return cb.and(predicates.stream().toArray(Predicate[]::new));
        };
    }
}
