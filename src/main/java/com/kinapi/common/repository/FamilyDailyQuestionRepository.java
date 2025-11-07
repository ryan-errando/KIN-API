package com.kinapi.common.repository;

import com.kinapi.common.entity.FamilyDailyQuestion;
import com.kinapi.common.entity.FamilyGroups;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface FamilyDailyQuestionRepository extends JpaRepository<FamilyDailyQuestion, UUID>, JpaSpecificationExecutor<FamilyDailyQuestion> {
    List<FamilyDailyQuestion> findByFamilyGroups(FamilyGroups familyGroups);
}
