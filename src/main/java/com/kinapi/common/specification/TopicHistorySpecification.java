package com.kinapi.common.specification;

import com.kinapi.common.entity.TopicHistory;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.UUID;

public class TopicHistorySpecification {
    private TopicHistorySpecification() {}

    public static Specification<TopicHistory> hasTopicName(String topicName) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.like(criteriaBuilder.lower(root.get("topicText")), "%" + topicName.toLowerCase() + "%");
    }

    public static Specification<TopicHistory> belongsToUser(UUID userId) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("user").get("id"), userId);
    }

    public static Specification<TopicHistory> hasCategories(List<String> categories) {
        return (root, query, criteriaBuilder) -> {
            if (categories == null || categories.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            return root.get("category").in(categories);
        };
    }

    public static Specification<TopicHistory> isFavorite(Boolean favorite) {
        return (root, query, criteriaBuilder) -> {
            if (favorite == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("isFavorite"), favorite);
        };
    }
}
