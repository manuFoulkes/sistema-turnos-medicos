package com.manuelfoulkes.turnos_medicos.repositories;

import com.manuelfoulkes.turnos_medicos.entities.Patient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PatientRepository extends JpaRepository<Patient, Long> {

    Optional<Patient> findByNationalId(String nationalId);
}
