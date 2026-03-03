package com.manuelfoulkes.turnos_medicos.dtos.responses;

public record PatientResponseDTO(
        Long id,
        String name,
        String lastName,
        String nationalId,
        String email,
        String phoneNumber
) {
}
