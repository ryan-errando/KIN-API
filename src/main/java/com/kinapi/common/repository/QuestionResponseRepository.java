package com.kinapi.common.repository;

import com.kinapi.common.entity.QuestionResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface QuestionResponseRepository extends JpaRepository<QuestionResponse, UUID>, JpaSpecificationExecutor<QuestionResponse> {
}
