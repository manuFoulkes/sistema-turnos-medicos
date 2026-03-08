package com.manuelfoulkes.turnos_medicos.services;

import com.manuelfoulkes.turnos_medicos.dtos.requests.AppointmentRequestDTO;
import com.manuelfoulkes.turnos_medicos.dtos.responses.SpecialtyResponseDTO;
import com.manuelfoulkes.turnos_medicos.dtos.responses.DoctorResponseDTO;
import com.manuelfoulkes.turnos_medicos.dtos.responses.PatientResponseDTO;
import com.manuelfoulkes.turnos_medicos.dtos.responses.AppointmentResponseDTO;
import com.manuelfoulkes.turnos_medicos.entities.*;
import com.manuelfoulkes.turnos_medicos.exceptions.custom.InvalidOperationException;
import com.manuelfoulkes.turnos_medicos.exceptions.custom.ResourceNotFoundException;
import com.manuelfoulkes.turnos_medicos.exceptions.custom.UnauthorizedOperationException;
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

    // TODO: Implementar mappers y limpiar
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
        newAppointment.setDateTime(appointmentRequest.dateTime());
        newAppointment.setPatient(patient);
        newAppointment.setDoctor(doctor);

        newAppointment = appointmentRepository.save(newAppointment);

        PatientResponseDTO patientResponse = new PatientResponseDTO(
                patient.getId(),
                patient.getName(),
                patient.getLastName(),
                patient.getNationalId(),
                patient.getEmail(),
                patient.getPhoneNumber()
        );

        Specialty specialty = doctor.getSpecialty();

        SpecialtyResponseDTO specialtyResponse = new SpecialtyResponseDTO(
                specialty.getId(),
                specialty.getName()
        );

        DoctorResponseDTO doctorResponse =  new DoctorResponseDTO(
                doctor.getId(),
                doctor.getName(),
                doctor.getLastName(),
                doctor.getLicenseNumber(),
                specialtyResponse
        );

        return new AppointmentResponseDTO(
                newAppointment.getId(),
                newAppointment.getDateTime(),
                newAppointment.getStatus(),
                patientResponse,
                doctorResponse,
                newAppointment.getDateTime()
        );
    }

    // TODO: Implementar mapppers y limpiar
    public AppointmentResponseDTO updateAppointment(Long patientId, Long appointmentId, AppointmentRequestDTO appointmentRequest) {
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
        LocalDateTime limitHour = appointment.getDateTime().minusHours(48);

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

        appointment.setDateTime(appointmentRequest.dateTime());
        appointment.setDoctor(doctor);

        Appointment updatedAppointment = appointmentRepository.save(appointment);
        Specialty specialty = doctor.getSpecialty();

        PatientResponseDTO pacienteResponse = new PatientResponseDTO(
                patient.getId(),
                patient.getName(),
                patient.getLastName(),
                patient.getNationalId(),
                patient.getEmail(),
                patient.getPhoneNumber()
        );

        SpecialtyResponseDTO especialidadResponse = new SpecialtyResponseDTO(
                specialty.getId(),
                specialty.getName()
        );

        DoctorResponseDTO medicoResponse = new DoctorResponseDTO(
                doctor.getId(),
                doctor.getName(),
                doctor.getLastName(),
                doctor.getLicenseNumber(),
                especialidadResponse
        );

        return new AppointmentResponseDTO(
                updatedAppointment.getId(),
                updatedAppointment.getDateTime(),
                updatedAppointment.getStatus(),
                pacienteResponse,
                medicoResponse,
                updatedAppointment.getCreationDate()
        );
    }

    // TODO: Implementar mappers y limpiar
    public AppointmentResponseDTO completeAppointment(Long appointmentId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("El turno no existe"));

        if(appointment.getStatus() != AppointmentStatus.RESERVED) {
            throw new InvalidOperationException("Solo se pueden completar turnos con estado RESERVADO");
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime windowStart = appointment.getDateTime().minusMinutes(30);
        LocalDateTime windowEnd = appointment.getDateTime().plusMinutes(10);

        if(now.isBefore(windowStart)) {
            throw new InvalidOperationException("El turno no se puede completar todavía");
        }

        if(now.isAfter(windowEnd)) {
            throw new InvalidOperationException("El turno ha expirado");
        }

        appointment.setStatus(AppointmentStatus.COMPLETED);
        Appointment appointmentCompleted = appointmentRepository.save(appointment);

        Patient patient = appointment.getPatient();

        PatientResponseDTO patientResponse = new PatientResponseDTO(
                patient.getId(),
                patient.getName(),
                patient.getLastName(),
                patient.getNationalId(),
                patient.getEmail(),
                patient.getPhoneNumber()
        );

        Doctor doctor = appointmentCompleted.getDoctor();

        Specialty specialty = doctor.getSpecialty();

        SpecialtyResponseDTO especialidadResponse = new SpecialtyResponseDTO(
                specialty.getId(),
                specialty.getName()
        );

        DoctorResponseDTO doctorResponse = new DoctorResponseDTO(
                doctor.getId(),
                doctor.getName(),
                doctor.getLastName(),
                doctor.getLicenseNumber(),
                especialidadResponse
        );

        return new AppointmentResponseDTO(
                appointmentCompleted.getId(),
                appointmentCompleted.getDateTime(),
                appointmentCompleted.getStatus(),
                patientResponse,
                doctorResponse,
                appointmentCompleted.getCreationDate()
        );
    }

    // TODO: Implementar mappers y limpiar
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
        LocalDateTime limitHour = appointment.getDateTime().minusHours(48);

        if(now.isAfter(limitHour)) {
            throw new InvalidOperationException("No se puede cancelar un turno con menos de 48 hs de anticipación");
        }

        appointment.setStatus(AppointmentStatus.CANCELLED);

        Appointment cancelledAppointment = appointmentRepository.save(appointment);

        PatientResponseDTO pacienteResponse = new PatientResponseDTO(
                patient.getId(),
                patient.getName(),
                patient.getLastName(),
                patient.getNationalId(),
                patient.getEmail(),
                patient.getPhoneNumber()
        );

        Doctor doctor = cancelledAppointment.getDoctor();

        Specialty specialty =  doctor.getSpecialty();

        SpecialtyResponseDTO specialtyResponse = new SpecialtyResponseDTO(
                specialty.getId(),
                specialty.getName()
        );

        DoctorResponseDTO doctorResponse = new DoctorResponseDTO(
                doctor.getId(),
                doctor.getName(),
                doctor.getLastName(),
                doctor.getLicenseNumber(),
                specialtyResponse
        );

        return new AppointmentResponseDTO(
                cancelledAppointment.getId(),
                cancelledAppointment.getDateTime(),
                cancelledAppointment.getStatus(),
                pacienteResponse,
                doctorResponse,
                cancelledAppointment.getDateTime()
        );
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
