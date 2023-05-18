package com.meteorinc.thegateway.domain.user;

import com.meteorinc.thegateway.domain.checkin.CheckIn;
import com.meteorinc.thegateway.infrastructure.converter.UUIDEntityConverter;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@AllArgsConstructor
@Builder
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "APP_USER")
@Getter
@Setter
public class AppUser implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "user_code", nullable = false, length = 36)
    @Convert(converter = UUIDEntityConverter.class)
    UUID userCode;

    @Column(name = "user_email", nullable = false, length = 100)
    String email;

    @Column(name = "user_password", nullable = false, length = 100)
    String password;

    @Column(name = "user_name", nullable = false, length = 100)
    String name;

    @Column(name = "user_document", nullable = false, length = 100)
    String document;

    @Column(name = "user_document_type", nullable = false, length = 50)
    String documentType;

    @Column(name = "dat_creation", nullable = false)
    LocalDateTime createdAt;

    @Column(name = "dat_update", nullable = false)
    LocalDateTime updatedAt;

    @Setter
    @OneToMany(mappedBy = "user",fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    List<Role> roles;

    @OneToOne(mappedBy = "appUser", cascade = CascadeType.ALL)
    CheckIn checkIn;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream().map(role -> new SimpleGrantedAuthority(role.getName().name())).collect(Collectors.toList());
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public AppUserDTO toDTO(){
        return AppUserDTO.builder()
                .name(name)
                .email(email)
                .document(document)
                .documentType(documentType)
                .build();
    }

}