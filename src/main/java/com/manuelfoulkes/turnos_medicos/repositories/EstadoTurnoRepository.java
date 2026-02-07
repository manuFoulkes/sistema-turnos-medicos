package com.manuelfoulkes.turnos_medicos.repositories;

import com.manuelfoulkes.turnos_medicos.entities.EstadoTurno;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EstadoTurnoRepository extends JpaRepository<EstadoTurno, Long> {
}
