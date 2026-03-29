package com.manuelfoulkes.turnos_medicos.repositories;

import com.manuelfoulkes.turnos_medicos.entities.AppointmentStatus;
import com.manuelfoulkes.turnos_medicos.entities.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    boolean existsByDoctorIdAndDateTimeAndStatusNot(Long doctorId, LocalDateTime dateTime, AppointmentStatus status);

    int countByPatientIdAndStatusAndDateTimeAfter(Long patientId, AppointmentStatus status, LocalDateTime dateTime);

    boolean existsByDoctorIdAndDateTimeAndStatusNotAndIdNot(
            Long doctorId,
            LocalDateTime dateTime,
            AppointmentStatus status,
            Long appointmentId
    );
}
