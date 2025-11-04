package com.kinapi.common.repository;

import com.kinapi.common.entity.DailyQuestionResponse;
import com.kinapi.common.entity.FamilyDailyQuestion;
import com.kinapi.common.entity.FamilyMembers;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface DailyQuestionResponseRepository extends JpaRepository<DailyQuestionResponse, UUID>, JpaSpecificationExecutor<DailyQuestionResponse> {
    Optional<DailyQuestionResponse> findByFamilyDailyQuestionAndFamilyMembers(FamilyDailyQuestion familyDailyQuestion, FamilyMembers familyMembers);
}
