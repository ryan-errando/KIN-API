package com.kinapi.common.repository;

import com.kinapi.common.entity.CalendarEvents;
import com.kinapi.common.entity.FamilyGroups;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CalendarEventsRepository extends JpaRepository<CalendarEvents, UUID>, JpaSpecificationExecutor<CalendarEvents> {
    
    @Query("SELECT ce FROM CalendarEvents ce WHERE ce.createdBy.group = :familyGroup")
    List<CalendarEvents> findAllByFamilyGroup(@Param("familyGroup") FamilyGroups familyGroup);
}
