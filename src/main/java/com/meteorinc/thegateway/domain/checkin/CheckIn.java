package com.meteorinc.thegateway.domain.checkin;

import com.meteorinc.thegateway.domain.event.Event;
import com.meteorinc.thegateway.domain.user.AppUser;
import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Setter
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "check_in")
public class CheckIn {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "USER_ID")
    AppUser appUser;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "EVENT_ID")
    Event event;

    @Column(name = "DAT_CHECK_IN", nullable = false)
    LocalDateTime checkInDate;

    @Column(name = "DAT_LAST_PRUNE", nullable = false)
    LocalDateTime lastPrune;

    @Column(name = "DAT_CHECK_OUT", nullable = false)
    LocalDateTime checkOutDate;

    @Column(name = "dat_creation", nullable = false)
    LocalDateTime createdAt;

    @Column(name = "dat_update", nullable = false)
    LocalDateTime updatedAt;
}
