package com.meteorinc.thegateway.interfaces.event.requests;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Request de criação de um evento.
 */
@Data
@AllArgsConstructor(onConstructor_ = @JsonCreator)
@Builder
public class EventDetailsRequest implements Serializable {

    /** Localização do evento */
    @NotNull
    LocationCreationRequest location;

    /** Nome do evento */
    @NotNull
    String name;

    /** Descrição do evento */
    @NotBlank
    String description;

    /** Flag que indica se deve ser feito validação da rede do usuario. */
    boolean isNetworkValidationEnabled;

    /** Data de inicio do evento. */
    @NotNull
    LocalDateTime startingDate;

    /** Duração do evento em horas. */
    @NotNull
    double durationHours;

}