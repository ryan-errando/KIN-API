package com.kinapi.common.repository;

import com.kinapi.common.entity.TopicHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TopicHistoryRepository extends JpaRepository<TopicHistory, UUID>, JpaSpecificationExecutor<TopicHistory> {
    List<TopicHistory> findByUserId(UUID userId);
}
