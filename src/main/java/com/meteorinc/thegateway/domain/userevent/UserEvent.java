package com.meteorinc.thegateway.domain.userevent;

import com.meteorinc.thegateway.domain.event.Event;
import com.meteorinc.thegateway.domain.user.User;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
import java.time.LocalDateTime;

@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Entity
@Table(name = "LOCATION")
public class UserEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "USER_ID")
    User user;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "EVENT_ID")
    Event event;

    @Column(name = "dat_check_in", nullable = false)
    LocalDateTime checkInDate;

    @Column(name = "dat_check_out", nullable = false)
    LocalDateTime checkOutDate;
}
