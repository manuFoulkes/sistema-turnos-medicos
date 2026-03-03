package com.manuelfoulkes.turnos_medicos.repositories;

import com.manuelfoulkes.turnos_medicos.entities.AppointmentStatus;
import com.manuelfoulkes.turnos_medicos.entities.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    boolean existsByDoctorIdAndDateTimeAndStatusNot(Long medicoId, LocalDateTime fechaHora, AppointmentStatus estado);

    int countByPatientIdAndStatusAndDateTimeAfter(Long pacienteId, AppointmentStatus estado, LocalDateTime fechaHora);

    boolean existsByDoctorIdAndDateTimeAndStatusNotAndIdNot(Long medicoId, LocalDateTime fechaHora, AppointmentStatus estado, Long turnoId);
}