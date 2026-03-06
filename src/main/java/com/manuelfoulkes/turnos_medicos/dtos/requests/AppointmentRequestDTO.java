package com.manuelfoulkes.turnos_medicos.dtos.requests;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDateTime;

public record AppointmentRequestDTO(

        @NotNull(message = "La fecha y la hora son obligatorias")
        LocalDateTime dateTime,

        @NotNull(message = "El id del paciente es obligatorio")
        @Positive
        Long patientId,

        @NotNull(message = "El id del médico es obligatorio")
        @Positive
        Long doctorId
) {
}
