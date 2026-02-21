package com.manuelfoulkes.turnos_medicos.dtos.responses;

public record PacienteResponseDTO(
        Long id,
        String nombre,
        String apellido,
        String dni,
        String email,
        String telefono
) {
}
