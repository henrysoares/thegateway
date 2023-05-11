package com.meteorinc.thegateway.domain.event;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EventRepository extends JpaRepository<Event, Long> {

    Optional<Event> findByEventCode(UUID eventCode);

    Optional<List<Event>> findByOwnerCode(UUID ownerCode);

}