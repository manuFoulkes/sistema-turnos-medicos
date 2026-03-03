package com.manuelfoulkes.turnos_medicos.dtos.responses;

import com.manuelfoulkes.turnos_medicos.entities.AppointmentStatus;

import java.time.LocalDateTime;

public record AppointmentResponseDTO(
        Long id,
        LocalDateTime dateTime,
        AppointmentStatus status,
        PatientResponseDTO patientResponse,
        DoctorResponseDTO doctorResponse,
        LocalDateTime creationDate
) {
}