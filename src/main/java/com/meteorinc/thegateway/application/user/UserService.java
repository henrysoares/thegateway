package com.meteorinc.thegateway.application.user;

import com.meteorinc.thegateway.domain.user.AppUser;
import com.meteorinc.thegateway.domain.user.Role;
import com.meteorinc.thegateway.domain.user.RoleType;
import com.meteorinc.thegateway.domain.user.UserRepository;
import com.meteorinc.thegateway.interfaces.user.requests.UserCreationRequest;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor(onConstructor_ = @Autowired)
@Slf4j
public class UserService implements UserDetailsService {

    UserRepository userRepository;

    PasswordEncoder passwordEncoder;

    TokenService tokenService;

    public AppUser findUserByToken(@NonNull final String rawToken){
        final String code = tokenService.getUserCodeOnToken(rawToken);

        return userRepository.findByUserCode(UUID.fromString(code)).orElseThrow(RuntimeException::new);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var test = userRepository.findByEmail(username);

        return test.orElseThrow(RuntimeException::new);
    }

    public void createUser(@NonNull final UserCreationRequest request){
        try{

            final AppUser user = AppUser.builder()
                    .email(request.getEmail())
                    .password(passwordEncoder.encode(request.getPassword()))
                    .name(request.getName())
                    .document(request.getDocument())
                    .documentType(request.getDocumentType())
                    .userCode(UUID.randomUUID())
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();

            final List<Role> roles = List.of(Role.initializeRole(user, RoleType.USER_ROLE));

            user.setRoles(roles);

            userRepository.save(user);
        }catch (Exception exception){
            log.error("Was not possible to create the user, cause=",exception);
            throw exception;
        }
    }

    public void addRole(@NonNull final UUID userCode, @NonNull final RoleType roleType){
        final AppUser user = userRepository.findByUserCode(userCode).orElseThrow(RuntimeException::new);

        user.setRoles(List.of(Role.initializeRole(user, roleType)));

        userRepository.save(user);
    }


}
