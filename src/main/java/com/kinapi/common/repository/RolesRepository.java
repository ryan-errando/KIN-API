package com.kinapi.common.repository;

import com.kinapi.common.entity.Roles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface RolesRepository extends JpaRepository<Roles, UUID>, JpaSpecificationExecutor<Roles> {
}
