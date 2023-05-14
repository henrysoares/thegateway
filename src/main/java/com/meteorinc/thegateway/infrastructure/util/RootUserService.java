package com.meteorinc.thegateway.infrastructure.util;

import com.meteorinc.thegateway.domain.user.AppUser;
import com.meteorinc.thegateway.domain.user.Role;
import com.meteorinc.thegateway.domain.user.RoleType;
import com.meteorinc.thegateway.domain.user.UserRepository;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class RootUserService {


    UserRepository userRepository;

    PasswordEncoder passwordEncoder;

    String rootName;

    String rootEmail;

    String password;

    boolean isRootUserEnabled;

    boolean isCustomPasswordEnabled;

    @Autowired
    public RootUserService(UserRepository userRepository, PasswordEncoder passwordEncoder, @Value("${root-user.details.name}") String rootName, @Value("${root-user.details.email}") String rootEmail, @Value("${root-user.initialize}") boolean isRootUserEnabled, @Value("${root-user.custom-password}") boolean isCustomPasswordEnabled, @Value("${root-user.details.password}") final String password) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.password = password;
        this.isCustomPasswordEnabled = isCustomPasswordEnabled;
        this.rootName = rootName;
        this.rootEmail = rootEmail;
        this.isRootUserEnabled = isRootUserEnabled;
    }

    @PostConstruct
    public void initRootUser(){
        if(!isRootUserEnabled) return;

        if(!isCustomPasswordEnabled){
            if(userRepository.findByEmail(rootEmail).isPresent()){
                return;
            }
        }
        userRepository.findByEmail(rootEmail).ifPresent(userRepository::delete);

        final String rootPassword = this.isCustomPasswordEnabled ? password : UUID.randomUUID().toString();

        final AppUser user = AppUser.builder()
                .name(rootName)
                .email(rootEmail)
                .password(passwordEncoder.encode(rootPassword))
                .userCode(UUID.randomUUID())
                .document("root")
                .documentType("root")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        final List<Role> rootRoles = List.of(Role.initializeRole(user, RoleType.ADMIN_ROLE));

        user.setRoles(rootRoles);

        userRepository.save(user);

        log.info("Root user created, password={}", rootPassword);

    }

    @PreDestroy
    public void finishRootUser(){
        if(!isRootUserEnabled) return;

        userRepository.findByEmail(rootEmail).ifPresent(userRepository::delete);
        log.info("Root user destroyed");
    }
}
