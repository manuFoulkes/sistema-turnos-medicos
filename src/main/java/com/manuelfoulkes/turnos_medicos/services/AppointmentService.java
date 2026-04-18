package com.manuelfoulkes.turnos_medicos.services;

import com.manuelfoulkes.turnos_medicos.dtos.requests.AppointmentRequestDTO;
import com.manuelfoulkes.turnos_medicos.dtos.responses.AppointmentResponseDTO;
import com.manuelfoulkes.turnos_medicos.entities.*;
import com.manuelfoulkes.turnos_medicos.exceptions.custom.InvalidOperationException;
import com.manuelfoulkes.turnos_medicos.exceptions.custom.ResourceNotFoundException;
import com.manuelfoulkes.turnos_medicos.exceptions.custom.UnauthorizedOperationException;
import com.manuelfoulkes.turnos_medicos.mappers.AppointmentMapper;
import com.manuelfoulkes.turnos_medicos.repositories.DoctorRepository;
import com.manuelfoulkes.turnos_medicos.repositories.PatientRepository;
import com.manuelfoulkes.turnos_medicos.repositories.AppointmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final AppointmentMapper appointmentMapper;

    // TODO: Chequear el uso de métodos helpers (no son necesarios)
    // TODO: limpiar
    public AppointmentResponseDTO bookAppointment(AppointmentRequestDTO appointmentRequest) {
        Long patientId = appointmentRequest.patientId();
        Long doctorId = appointmentRequest.doctorId();
        int maxAppointments = 3;

        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new ResourceNotFoundException("Paciente no encontrado"));

        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() ->  new ResourceNotFoundException("Medico no encontrado"));

        if(appointmentRequest.dateTime().isBefore(LocalDateTime.now())) {
            throw new InvalidOperationException("Fecha inválida");
        }

        if(appointmentIsBooking(doctorId, appointmentRequest.dateTime(), AppointmentStatus.CANCELLED)) {
            throw new InvalidOperationException("El medico ya tiene un turno asignado en ese horario");
        }

        int activesAppointments = getActivesAppointments(patientId, AppointmentStatus.RESERVED, LocalDateTime.now());

        if (activesAppointments > maxAppointments) {
            throw new InvalidOperationException("Cantidad de turnos por paciente excedida");
        }

        Appointment newAppointment =  new Appointment();
        newAppointment.setScheduleDateTime(appointmentRequest.dateTime());
        newAppointment.setPatient(patient);
        newAppointment.setDoctor(doctor);

        newAppointment = appointmentRepository.save(newAppointment);

        return appointmentMapper.toResponseDTO(newAppointment);
    }

    // TODO: limpiar
    public AppointmentResponseDTO updateAppointment(
            Long patientId,
            Long appointmentId,
            AppointmentRequestDTO appointmentRequest
    ) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new ResourceNotFoundException("El paciente no existe"));

        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("El turno no existe"));

        if(!appointment.getPatient().getId().equals(patientId)) {
            throw new UnauthorizedOperationException("El turno no pertenece al paciente");
        }

        if(appointment.getStatus().equals(AppointmentStatus.CANCELLED)) {
            throw new InvalidOperationException("El turno ya ha sido cancelado");
        }

        if(appointment.getStatus().equals(AppointmentStatus.COMPLETED)) {
            throw new InvalidOperationException("El turno ya ha sido completado");
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime limitHour = appointment.getScheduleDateTime().minusHours(48);

        if(now.isAfter(limitHour)) {
            throw new InvalidOperationException("No se puede modificar un turno con menos de 48 hs de anticipación");
        }

        Doctor doctor = doctorRepository.findById(appointmentRequest.doctorId())
                .orElseThrow(() -> new ResourceNotFoundException("El médico no existe"));

        boolean appointmentIsBooking = appointmentIsBooking(doctor.getId(), appointmentRequest.dateTime(), AppointmentStatus.CANCELLED, appointmentId);

        if(appointmentIsBooking) {
            throw new InvalidOperationException("El médico ya tiene un turno asignado en ese horario");
        }

        if(now.isAfter(appointmentRequest.dateTime())) {
            throw new InvalidOperationException("La fecha debe ser futura");
        }

        appointment.setScheduleDateTime(appointmentRequest.dateTime());
        appointment.setDoctor(doctor);

        Appointment updatedAppointment = appointmentRepository.save(appointment);

        return appointmentMapper.toResponseDTO(updatedAppointment);
    }

    // TODO: limpiar
    public AppointmentResponseDTO completeAppointment(Long appointmentId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("El turno no existe"));

        if(appointment.getStatus() != AppointmentStatus.RESERVED) {
            throw new InvalidOperationException("Solo se pueden completar turnos con estado RESERVADO");
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime windowStart = appointment.getScheduleDateTime().minusMinutes(30);
        LocalDateTime windowEnd = appointment.getScheduleDateTime().plusMinutes(10);

        if(now.isBefore(windowStart)) {
            throw new InvalidOperationException("El turno no se puede completar todavía");
        }

        if(now.isAfter(windowEnd)) {
            throw new InvalidOperationException("El turno ha expirado");
        }

        appointment.setStatus(AppointmentStatus.COMPLETED);

        Appointment appointmentCompleted = appointmentRepository.save(appointment);

        return appointmentMapper.toResponseDTO(appointmentCompleted);
    }

    // TODO: limpiar
    public AppointmentResponseDTO cancelAppointment(Long patientId, Long appointmentId) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new ResourceNotFoundException("Paciente no encontrado"));

        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Turno no encontrado"));

        if(!appointment.getPatient().getId().equals(patientId)) {
            throw new UnauthorizedOperationException("No existe un turno asignado con ese ID");
        }

        if(appointment.getStatus().equals(AppointmentStatus.CANCELLED)) {
            throw new InvalidOperationException("El turno ya fue cancelado");
        }

        if(appointment.getStatus().equals(AppointmentStatus.COMPLETED)) {
            throw new InvalidOperationException("El turno ya ha sido completado");
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime limitHour = appointment.getScheduleDateTime().minusHours(48);

        if(now.isAfter(limitHour)) {
            throw new InvalidOperationException("No se puede cancelar un turno con menos de 48 hs de anticipación");
        }

        appointment.setStatus(AppointmentStatus.CANCELLED);

        Appointment cancelledAppointment = appointmentRepository.save(appointment);

        return appointmentMapper.toResponseDTO(cancelledAppointment);
    }

    private boolean appointmentIsBooking(Long doctorId, LocalDateTime dateTime, AppointmentStatus appointmentStatus) {
        return appointmentRepository.existsByDoctorIdAndDateTimeAndStatusNot(doctorId, dateTime, appointmentStatus);
    }

    private boolean appointmentIsBooking(Long doctorId, LocalDateTime dateTime, AppointmentStatus appointmentStatus, Long appointmentId) {
        return appointmentRepository.existsByDoctorIdAndDateTimeAndStatusNotAndIdNot(doctorId, dateTime, appointmentStatus, appointmentId);
    }

    private int getActivesAppointments(Long patientId, AppointmentStatus appointmentStatus, LocalDateTime dateTime) {
        return appointmentRepository.countByPatientIdAndStatusAndDateTimeAfter(patientId, appointmentStatus, dateTime);
    }
}
