package com.meteorinc.thegateway.domain.event;

import com.meteorinc.thegateway.domain.location.Location;
import com.meteorinc.thegateway.infrastructure.converter.UUIDEntityConverter;
import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "certificate")
public class Certificate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "certificate_code", nullable = false, length = 36)
    @Convert(converter = UUIDEntityConverter.class)
    UUID certificateCode;

    @Column(name = "certificate_description", nullable = false)
    String description;

    @Column(name = "certificate_content", columnDefinition = "MEDIUMBLOB")
    byte[] content;

    @Column(name = "certificate_metadata", nullable = false)
    String metadata;

    @Column(name = "dat_creation", nullable = false)
    LocalDateTime createdAt;

    @Column(name = "dat_update", nullable = false)
    LocalDateTime updatedAt;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "event_id")
    Event event;
}
