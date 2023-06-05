package com.meteorinc.thegateway.domain.event;

import com.meteorinc.thegateway.domain.user.AppUser;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EventRepository extends JpaRepository<Event, Long> {

    Optional<Event> findByEventCode(UUID eventCode);

    Optional<List<Event>> findByOwnerCode(UUID ownerCode);

    Optional<List<Event>> findByStatusAndStartsAtLessThan(@NonNull final EventStatus status,
                                                          @NonNull final LocalDateTime targetTime);

    Optional<List<Event>> findByStatusAndFinishesAtLessThan(@NonNull final EventStatus status,
                                                            @NonNull final LocalDateTime targetTime);

  @Query(
      value =
          "select au.* from event e "
              + "inner join check_in ci on ci.event_id = e.id  "
              + "inner join app_user au on ci.user_id = au.id  "
              + "where ci.certificate_process_status = 'NONE' "
              + "and e.event_status in ('FINISHED','FORCED_FINISHED') "
              + "limit 50",
          nativeQuery = true)
  Optional<List<AppUser>> findAllEligibleUserForCertification();


}