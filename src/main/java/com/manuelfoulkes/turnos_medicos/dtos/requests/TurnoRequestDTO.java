package com.manuelfoulkes.turnos_medicos.dtos.requests;

import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;

public record TurnoRequestDTO(

        @NotBlank(message = "La fecha y la hora son obligatorias")
        LocalDateTime fechaHora,

        @NotBlank(message = "El id del paciente es obligatorio")
        Long pacienteId,

        @NotBlank(message = "El id del médico es obligatorio")
        Long medicoId
) {
}
