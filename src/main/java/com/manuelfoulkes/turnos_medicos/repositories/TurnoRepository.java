package com.manuelfoulkes.turnos_medicos.repositories;

import com.manuelfoulkes.turnos_medicos.entities.EstadoTurno;
import com.manuelfoulkes.turnos_medicos.entities.Turno;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface TurnoRepository extends JpaRepository<Turno, Long> {

    boolean existsByMedicoIdAndFechaHoraAndEstadoNot(Long medicoId, LocalDateTime fechaHora, EstadoTurno estado);

    int countByPacienteIdAndEstadoAndFechaHoraAfter(Long pacienteId, EstadoTurno estado, LocalDateTime fechaHora);
}
