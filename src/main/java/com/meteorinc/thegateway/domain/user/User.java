package com.meteorinc.thegateway.domain.user;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Entity
@Table(name = "USER")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "user_code", nullable = false, length = 36)
    UUID userCode;

    @Column(name = "user_email", nullable = false, length = 100)
    String email;

    @Column(name = "user_password", nullable = false, length = 100)
    String password;

    @Column(name = "dat_creation", nullable = false)
    LocalDateTime createdAt;

    @Column(name = "dat_update", nullable = false)
    LocalDateTime updatedAt;

}