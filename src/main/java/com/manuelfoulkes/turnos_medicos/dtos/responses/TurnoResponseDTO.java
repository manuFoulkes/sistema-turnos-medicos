package com.manuelfoulkes.turnos_medicos.dtos.responses;

import com.manuelfoulkes.turnos_medicos.entities.EstadoTurno;

import java.time.LocalDateTime;

public record TurnoResponseDTO(
        Long id,
        LocalDateTime fechaHora,
        EstadoTurno estado,
        PacienteResponseDTO pacienteResponse,
        MedicoResponseDTO medicoResponse,
        LocalDateTime fechaCreacion
) {
}