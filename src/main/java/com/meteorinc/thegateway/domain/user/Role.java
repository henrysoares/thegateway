package com.meteorinc.thegateway.domain.user;

import com.meteorinc.thegateway.domain.location.Location;
import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
import java.time.LocalDateTime;

@AllArgsConstructor
@Builder
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "ROLE")
@Getter
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "role_name", length = 100)
            @Enumerated(EnumType.STRING)
    RoleType name;

    @Column(name = "dat_creation", nullable = false)
    LocalDateTime createdAt;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    AppUser user;

    public static Role initializeRole(@NonNull final AppUser user, @NonNull final RoleType name){
        return Role.builder()
                .user(user)
                .name(name)
                .createdAt(LocalDateTime.now())
                .build();
    }

}
