package com.manuelfoulkes.turnos_medicos.dtos.requests;

import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;

public record AppointmentRequestDTO(

        @NotBlank(message = "La fecha y la hora son obligatorias")
        LocalDateTime dateTime,

        @NotBlank(message = "El id del paciente es obligatorio")
        Long patientId,

        @NotBlank(message = "El id del médico es obligatorio")
        Long doctorId
) {
}
