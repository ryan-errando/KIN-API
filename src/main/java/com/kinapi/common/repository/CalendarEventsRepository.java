package com.kinapi.common.repository;

import com.kinapi.common.entity.CalendarEvents;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CalendarEventsRepository extends JpaRepository<CalendarEvents, UUID>, JpaSpecificationExecutor<CalendarEvents> {
}
