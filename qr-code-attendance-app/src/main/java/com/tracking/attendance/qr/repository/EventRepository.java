package com.tracking.attendance.qr.repository;

import com.tracking.attendance.qr.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.Collection;

@Repository
public interface EventRepository extends JpaRepository<Event, Integer> {
    Collection<Event> findByStartDateBetween(ZonedDateTime dateFrom, ZonedDateTime dateTo);

    boolean existsByEventsGroupId(String id);

    void deleteByEventsGroupId(String id);

    Collection<Event> findByEventsGroupId(String id);
}