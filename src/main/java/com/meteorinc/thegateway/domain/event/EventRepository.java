package com.meteorinc.thegateway.domain.event;

import com.meteorinc.thegateway.domain.user.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EventRepository extends JpaRepository<Event, Long> {

    Optional<Event> findByEventCode(UUID eventCode);

    Optional<List<Event>> findByOwnerCode(UUID ownerCode);

  @Query(
      value =
          "select au.* from event e "
              + "inner join check_in ci on ci.event_id = e.id  "
              + "inner join app_user au on ci.user_id = au.id  "
              + "where ci.certificate_process_status = 'NONE' "
              + "and e.event_status = 'FINISHED'  "
              + "limit 50",
          nativeQuery = true)
  Optional<List<AppUser>> findAllEligibleUserForCertification();
}