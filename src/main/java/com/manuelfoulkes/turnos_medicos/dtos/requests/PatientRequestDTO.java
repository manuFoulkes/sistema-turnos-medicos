package com.manuelfoulkes.turnos_medicos.dtos.requests;

import jakarta.validation.constraints.NotBlank;

public record PatientRequestDTO(

        @NotBlank(message = "El nombre es obligatorio")
        String name,

        @NotBlank(message = "El apellido es obligatorio")
        String lastName,

        @NotBlank(message = "El DNI es obligatorio")
        String nationalId,

        @NotBlank(message = "El email es obligatorio")
        String email,

        @NotBlank(message = "El telefono es obligatorio")
        String phoneNumber
) {
}
