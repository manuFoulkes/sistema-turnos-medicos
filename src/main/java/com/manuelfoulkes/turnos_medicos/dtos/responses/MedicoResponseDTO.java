package com.manuelfoulkes.turnos_medicos.dtos.responses;

public record MedicoResponseDTO(
        Long id,
        String nombre,
        String apellido,
        String matricula,
        EspecialidadResponseDTO especialidad
) {
}
