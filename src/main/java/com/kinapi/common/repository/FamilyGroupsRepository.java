package com.kinapi.common.repository;

import com.kinapi.common.entity.FamilyGroups;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface FamilyGroupsRepository extends JpaRepository<FamilyGroups, UUID>, JpaSpecificationExecutor<FamilyGroups> {
    boolean existsByInvitationCode(String invitationCode);
}
