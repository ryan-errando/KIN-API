package com.kinapi.common.repository;

import com.kinapi.common.entity.DailyQuestionResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface DailyQuestionResponseRepository extends JpaRepository<DailyQuestionResponse, UUID>, JpaSpecificationExecutor<DailyQuestionResponse> {
}
