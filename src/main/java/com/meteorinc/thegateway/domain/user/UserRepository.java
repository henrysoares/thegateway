package com.meteorinc.thegateway.domain.user;

import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<AppUser, Long> {
    Optional<AppUser> findByEmail(@NonNull final String email);

    Optional<AppUser> findByUserCode(@NonNull final UUID userCode);
}