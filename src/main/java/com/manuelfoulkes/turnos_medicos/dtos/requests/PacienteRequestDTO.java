package com.manuelfoulkes.turnos_medicos.dtos.requests;

import jakarta.validation.constraints.NotBlank;

public record PacienteRequestDTO(

        @NotBlank(message = "El nombre es obligatorio")
        String nombre,

        @NotBlank(message = "El apellido es obligatorio")
        String apellido,

        @NotBlank(message = "El DNI es obligatorio")
        String dni,

        @NotBlank(message = "El email es obligatorio")
        String email,

        @NotBlank(message = "El telefono es obligatorio")
        String telefono
) {
}
