package com.kinapi.common.repository;

import com.kinapi.common.entity.FamilyGroups;
import com.kinapi.common.entity.FamilyMembers;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface FamilyMembersRepository extends JpaRepository<FamilyMembers, UUID>, JpaSpecificationExecutor<FamilyMembers> {
    long countByGroup(FamilyGroups group);
}
