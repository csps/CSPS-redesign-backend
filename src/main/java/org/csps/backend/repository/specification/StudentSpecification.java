package org.csps.backend.repository.specification;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.csps.backend.domain.dtos.request.StudentSearchDTO;
import org.csps.backend.domain.entities.Student;
import org.csps.backend.domain.entities.StudentMembership;
import org.csps.backend.domain.entities.UserAccount;
import org.csps.backend.domain.entities.UserProfile;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Subquery;

@Component
public class StudentSpecification {
    
    /**
     * build a dynamic specification for filtering non-member students
     * applies predicates based on non-null search criteria in studentsearchdto
     * supports filtering by student name, student id, and year level
     * excludes students who have active memberships
     */
    public static Specification<Student> nonMembersWithFilters(StudentSearchDTO searchDTO) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            // subquery to find students with active memberships
            Subquery<String> membershipSubquery = query.subquery(String.class);
            org.springframework.data.jpa.domain.Root<StudentMembership> membershipRoot = membershipSubquery.from(StudentMembership.class);
            membershipSubquery.select(membershipRoot.get("student").get("studentId"))
                    .where(cb.equal(membershipRoot.get("active"), true));
            
            // exclude students with active memberships
            predicates.add(cb.not(cb.in(root.get("studentId")).value(membershipSubquery)));
            
            if (Objects.nonNull(searchDTO)) {
                // join with useraccount entity
                Join<Student, UserAccount> userAccountJoin = root.join("userAccount", JoinType.LEFT);
                
                // join with userprofile entity
                Join<UserAccount, UserProfile> userProfileJoin = userAccountJoin.join("userProfile", JoinType.LEFT);
                
                // filter by student name (first name or last name)
                if (Objects.nonNull(searchDTO.getStudentName()) && !searchDTO.getStudentName().isEmpty()) {
                    String nameLike = "%" + searchDTO.getStudentName().toLowerCase() + "%";
                    predicates.add(
                        cb.or(
                            cb.like(cb.lower(userProfileJoin.get("firstName")), nameLike),
                            cb.like(cb.lower(userProfileJoin.get("lastName")), nameLike)
                        )
                    );
                }
                
                // filter by student id
                if (Objects.nonNull(searchDTO.getStudentId()) && !searchDTO.getStudentId().isEmpty()) {
                    predicates.add(cb.equal(root.get("studentId"), searchDTO.getStudentId()));
                }
                
                // filter by year level
                if (Objects.nonNull(searchDTO.getYearLevel())) {
                    predicates.add(cb.equal(root.get("yearLevel"), searchDTO.getYearLevel()));
                }
            }
            
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
