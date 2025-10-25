package com.kinapi.common.repository;

import com.kinapi.common.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/*
JpaRepository<nama class, type variable dari @id>
JpaSpecificationExecutor<nama class>
 */

@Repository
public interface UserRepository extends JpaRepository<Users, UUID>, JpaSpecificationExecutor<Users> {
    Optional<Users> findByEmail(String email);
}
