package com.meteorinc.thegateway.domain.checkin;

import com.meteorinc.thegateway.domain.event.Event;
import com.meteorinc.thegateway.domain.user.AppUser;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CheckInRepository extends JpaRepository<CheckIn, Long> {

    Optional<CheckIn> findByAppUserAndEvent(@NonNull final AppUser user, @NonNull final Event event);

    @Query(
      value =
          "select ci.* from check_in ci "
              + "inner join event e on e.id = ci.event_id "
              + "where ci.certificate_process_status = 'NONE' "
              + "and e.event_status in ('FINISHED', 'FORCED_FINISHED') "
              + "limit 50",
            nativeQuery = true)
    Optional<List<CheckIn>> findEligibleUsersForCertificateGeneration();

    @Query(
            value =
                    "select ci.* from check_in ci "
                            + "inner join event e on e.id = ci.event_id "
                            + "where ci.certificate_process_status = 'DONE' "
                            + "and e.event_status in ('FINISHED', 'FORCED_FINISHED') "
                            + "limit 50",
            nativeQuery = true)
    Optional<List<CheckIn>> findEligibleUsersForCertificateSending();
}
