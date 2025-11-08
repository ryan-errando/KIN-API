package com.kinapi.common.repository;

import com.kinapi.common.entity.DailyQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface DailyQuestionRepository extends JpaRepository<DailyQuestion, UUID>, JpaSpecificationExecutor<DailyQuestion> {
}
