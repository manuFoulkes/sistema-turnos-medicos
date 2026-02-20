package com.manuelfoulkes.turnos_medicos.dtos.requests;

import com.manuelfoulkes.turnos_medicos.entities.EstadoTurno;

import java.time.LocalDateTime;

public record TurnoRequestDTO(
        LocalDateTime fechaHora,
        EstadoTurno estado,
        PacienteRequestDTO pacienteRequest,
        MedicoRequestDTO medicoRequest,
        LocalDateTime fechaCreacion
) {
}
