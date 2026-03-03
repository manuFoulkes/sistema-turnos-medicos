package com.manuelfoulkes.turnos_medicos.dtos.responses;

public record DoctorResponseDTO(
        Long id,
        String name,
        String lastName,
        String licenseNumber,
        SpecialtyResponseDTO specialtyResponse
) {
}
