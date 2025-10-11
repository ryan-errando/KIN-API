package com.kinapi.common.specification;

import com.kinapi.common.entity.CalendarEvents;
import com.kinapi.common.entity.FamilyGroups;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class CalendarEventsSpecification {

    public static Specification<CalendarEvents> filterByDateTimeRange(LocalDateTime startDate, LocalDateTime endDate) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (startDate != null && endDate != null) {
                Predicate eventStartsBeforeRangeEnd = criteriaBuilder.lessThanOrEqualTo(root.get("startTime"), endDate);
                Predicate eventEndsAfterRangeStart = criteriaBuilder.greaterThanOrEqualTo(root.get("endTime"), startDate);
                predicates.add(criteriaBuilder.and(eventStartsBeforeRangeEnd, eventEndsAfterRangeStart));
            } else if (startDate != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("startTime"), startDate));
            } else if (endDate != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("startTime"), endDate));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    public static Specification<CalendarEvents> filterByFamilyGroup(FamilyGroups familyGroup) {
        return (root, query, criteriaBuilder) -> {
            if (familyGroup == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("createdBy").get("group"), familyGroup);
        };
    }
}
