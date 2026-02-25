package org.csps.backend.repository.specification;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.csps.backend.domain.dtos.request.OrderSearchDTO;
import org.csps.backend.domain.entities.Order;
import org.csps.backend.domain.entities.Student;
import org.csps.backend.domain.entities.UserAccount;
import org.csps.backend.domain.entities.UserProfile;
import org.csps.backend.domain.enums.OrderStatus;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;


@Component
public class OrderSpecification {
    
    /**
     * build a dynamic specification for order filtering
     * applies predicates based on non-null search criteria in ordersearchdto
     * supports filtering by student name, student id, status, and date range
     */
    public static Specification<Order> withFilters(OrderSearchDTO searchDTO) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            if (Objects.nonNull(searchDTO)) {
                // join with student entity
                Join<Order, Student> studentJoin = root.join("student", JoinType.LEFT);
                
                // join with useraccount entity
                Join<Student, UserAccount> userAccountJoin = studentJoin.join("userAccount", JoinType.LEFT);
                
                // join with userprofile entity
                Join<UserAccount, UserProfile> userProfileJoin = userAccountJoin.join("userProfile", JoinType.LEFT);
                
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
                
                // filter by student id
                if (Objects.nonNull(searchDTO.getStudentId()) && !searchDTO.getStudentId().isEmpty() && searchDTO.getStudentId() != null) {
                    predicates.add(cb.equal(studentJoin.get("studentId"), searchDTO.getStudentId().trim()));
                }
                
                // filter by order status
                if (Objects.nonNull(searchDTO.getStatus()) && !searchDTO.getStatus().isEmpty()) {
                    try {
                        OrderStatus status = OrderStatus.valueOf(searchDTO.getStatus().toUpperCase());
                        predicates.add(cb.equal(root.get("orderStatus"), status));
                    } catch (IllegalArgumentException e) {
                        // invalid status enum value, skip this filter
                    }
                }
                
                // filter by start date
                if (Objects.nonNull(searchDTO.getStartDate())) {
                    predicates.add(cb.greaterThanOrEqualTo(root.get("orderDate"), searchDTO.getStartDate()));
                }
                
                // filter by end date
                if (Objects.nonNull(searchDTO.getEndDate())) {
                    predicates.add(cb.lessThanOrEqualTo(root.get("orderDate"), searchDTO.getEndDate()));
                }
            }
            
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
