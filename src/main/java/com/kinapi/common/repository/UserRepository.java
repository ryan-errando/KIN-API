package com.kinapi.common.repository;

import com.kinapi.common.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<Users, UUID>, JpaSpecificationExecutor<Users> {
    Users findByEmail(String email);
}
