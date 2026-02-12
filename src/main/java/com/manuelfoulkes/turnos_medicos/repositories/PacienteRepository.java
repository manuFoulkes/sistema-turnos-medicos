package com.manuelfoulkes.turnos_medicos.repositories;

import com.manuelfoulkes.turnos_medicos.entities.Paciente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PacienteRepository extends JpaRepository<Paciente, Long> {

    Optional<Paciente> findByDni(String dni);
}
