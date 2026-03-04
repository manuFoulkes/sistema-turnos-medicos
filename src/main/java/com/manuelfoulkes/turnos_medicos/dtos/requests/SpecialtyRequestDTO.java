package com.manuelfoulkes.turnos_medicos.dtos.requests;

import jakarta.validation.constraints.NotBlank;

public record SpecialtyRequestDTO(
        @NotBlank(message = "El nombre es obligatorio")
        String name
) {
}
