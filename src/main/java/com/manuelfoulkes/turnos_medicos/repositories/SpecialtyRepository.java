package com.manuelfoulkes.turnos_medicos.repositories;

import com.manuelfoulkes.turnos_medicos.entities.Specialty;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SpecialtyRepository extends JpaRepository<Specialty, Long> {
    Optional<Specialty> findByName(String name);
}
