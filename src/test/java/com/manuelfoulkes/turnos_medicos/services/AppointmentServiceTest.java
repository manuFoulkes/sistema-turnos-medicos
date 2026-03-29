package com.manuelfoulkes.turnos_medicos.services;

import com.manuelfoulkes.turnos_medicos.dtos.requests.AppointmentRequestDTO;
import com.manuelfoulkes.turnos_medicos.dtos.responses.AppointmentResponseDTO;
import com.manuelfoulkes.turnos_medicos.dtos.responses.DoctorResponseDTO;
import com.manuelfoulkes.turnos_medicos.dtos.responses.PatientResponseDTO;
import com.manuelfoulkes.turnos_medicos.dtos.responses.SpecialtyResponseDTO;
import com.manuelfoulkes.turnos_medicos.entities.*;
import com.manuelfoulkes.turnos_medicos.exceptions.custom.InvalidOperationException;
import com.manuelfoulkes.turnos_medicos.exceptions.custom.ResourceNotFoundException;
import com.manuelfoulkes.turnos_medicos.exceptions.custom.UnauthorizedOperationException;
import com.manuelfoulkes.turnos_medicos.mappers.AppointmentMapper;
import com.manuelfoulkes.turnos_medicos.repositories.AppointmentRepository;
import com.manuelfoulkes.turnos_medicos.repositories.DoctorRepository;
import com.manuelfoulkes.turnos_medicos.repositories.PatientRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AppointmentServiceTest {

    @Mock
    AppointmentRepository appointmentRepository;

    @Mock
    DoctorRepository doctorRepository;

    @Mock
    PatientRepository patientRepository;

    @Mock
    AppointmentMapper appointmentMapper;

    @InjectMocks
    AppointmentService appointmentService;

    @Test
    void bookAppointment_whenRequestIsValid_returnsAppointmentResponseDTO() {
        // Given (Arrange)
        Long patientId = 1L;
        Long doctorId = 1L;
        LocalDateTime futureDateTime = LocalDateTime.now().plusDays(5);

        AppointmentRequestDTO request = new AppointmentRequestDTO(
                futureDateTime,
                patientId,
                doctorId
        );

        Patient patient = new Patient(
                1L,
                "Carlos",
                "Gomez",
                "394485839",
                "cgomez@gmail.com",
                "542983478634"
        );

        Specialty specialty = new Specialty(1L, "Cardiology");

        Doctor doctor = new Doctor(
                1L,
                "Horacio",
                "Martinez",
                "AB9283",
                specialty
        );

        Appointment savedAppointment = new Appointment(
                1L,
                futureDateTime,
                AppointmentStatus.RESERVED,
                patient,
                doctor,
                LocalDateTime.now()
        );

        PatientResponseDTO patientResponse = new PatientResponseDTO(
                1L,
                "Carlos",
                "Gomez",
                "394485839",
                "cgomez@gmail.com",
                "542983478634"
        );

        SpecialtyResponseDTO specialtyResponse = new SpecialtyResponseDTO(1L, "Cardiology");

        DoctorResponseDTO doctorResponse = new DoctorResponseDTO(
                1L,
                "Horacio",
                "Martinez",
                "AB9283",
                specialtyResponse
        );

        AppointmentResponseDTO expectedResponse = new AppointmentResponseDTO(
                1L,
                futureDateTime,
                AppointmentStatus.RESERVED,
                patientResponse,
                doctorResponse,
                LocalDateTime.now()
        );

        when(patientRepository.findById(patientId)).thenReturn(Optional.of(patient));
        when(doctorRepository.findById(doctorId)).thenReturn(Optional.of(doctor));

        when(appointmentRepository.existsByDoctorIdAndDateTimeAndStatusNot(
                doctorId,
                futureDateTime,
                AppointmentStatus.CANCELLED
        )).thenReturn(false);

        when(appointmentRepository.countByPatientIdAndStatusAndDateTimeAfter(
                eq(patientId),
                eq(AppointmentStatus.RESERVED),
                any(LocalDateTime.class)
        )).thenReturn(2);

        when(appointmentRepository.save(any(Appointment.class))).thenReturn(savedAppointment);
        when(appointmentMapper.toResponseDTO(savedAppointment)).thenReturn(expectedResponse);

        // When (Act)
        AppointmentResponseDTO actualResponse = appointmentService.bookAppointment(request);

        // Then (Assert)
        assertNotNull(actualResponse);
        assertEquals(expectedResponse.id(),  actualResponse.id());
        assertEquals(expectedResponse.status(), actualResponse.status());

        verify(patientRepository, times(1)).findById(patientId);
        verify(doctorRepository, times(1)).findById(doctorId);
        verify(appointmentRepository, times(1)).save(any(Appointment.class));
        verify(appointmentMapper, times(1)).toResponseDTO(any(Appointment.class));
    }

    @Test
    void bookAppointment_whenPatientDoesNotExists_throwsResourceNotFoundException() {
        // Given (Arrange)
        Long patientId = 1L;
        Long doctorId = 1L;
        LocalDateTime futureDateTime = LocalDateTime.now().plusDays(5);

        AppointmentRequestDTO request = new AppointmentRequestDTO(
                futureDateTime,
                patientId,
                doctorId
        );

        when(patientRepository.findById(patientId)).thenReturn(Optional.empty());

        // When & Then (Act & Assert)
        assertThrows(ResourceNotFoundException.class, () -> {
            appointmentService.bookAppointment(request);
        });

        verify(patientRepository, times(1)).findById(patientId);
        verify(appointmentRepository, never()).save(any(Appointment.class));
    }

    @Test
    void bookAppointment_whenDoctorDoesNotExists_throwsResourceNotFoundException() {
        // Given (Arrange)
        Long patientId = 1L;
        Long doctorId = 1L;
        LocalDateTime futureDateTime = LocalDateTime.now().plusDays(5);

        AppointmentRequestDTO request = new AppointmentRequestDTO(
                futureDateTime,
                patientId,
                doctorId
        );

        Patient patient = new Patient(
                1L,
                "Carlos",
                "Gomez",
                "394485839",
                "cgomez@gmail.com",
                "+542983478634"
        );

        when(patientRepository.findById(patientId)).thenReturn(Optional.of(patient));
        when(doctorRepository.findById(doctorId)).thenReturn(Optional.empty());

        // When & Then (Act & Assert)
        assertThrows(ResourceNotFoundException.class, () -> {
            appointmentService.bookAppointment(request);
        });

        verify(patientRepository, times(1)).findById(patientId);
        verify(doctorRepository, times(1)).findById(doctorId);
        verify(appointmentRepository, never()).save(any(Appointment.class));
    }

    @Test
    void bookAppointment_whenDateTimeIsInPast_throwsInvalidOperationException() {
        // Given (Arrange)
        Long patientId = 1L;
        Long doctorId = 1L;
        LocalDateTime futureDateTime = LocalDateTime.now().minusDays(5);

        AppointmentRequestDTO request = new AppointmentRequestDTO(
                futureDateTime,
                patientId,
                doctorId
        );

        Patient patient = new Patient(
                patientId,
                "Carlos",
                "Gomez",
                "394485839",
                "cgomez@gmail.com",
                "+542983478634"
        );

        Specialty specialty = new Specialty(1L, "Cardiology");

        Doctor doctor = new Doctor(
                doctorId,
                "Javier",
                "Bustamante",
                "AF9082",
                specialty
        );

        when(patientRepository.findById(patientId)).thenReturn(Optional.of(patient));
        when(doctorRepository.findById(doctorId)).thenReturn(Optional.of(doctor));

        // When & Then (Act & Assert)
        assertThrows(InvalidOperationException.class, () -> {
            appointmentService.bookAppointment(request);
        });

        verify(patientRepository, times(1)).findById(patientId);
        verify(doctorRepository, times(1)).findById(doctorId);
        verify(appointmentRepository, never()).save(any(Appointment.class));
    }

    @Test
    void bookAppointment_whenDoctorIsUnavailable_throwsInvalidOperationException() {
        //Given (Arrange)
        Long patientId = 1L;
        Long doctorId = 1L;
        LocalDateTime futureDateTime = LocalDateTime.now().plusDays(10);

        AppointmentRequestDTO request = new AppointmentRequestDTO(
                futureDateTime,
                patientId,
                doctorId
        );

        Patient patient = new Patient(
                patientId,
                "Carlos",
                "Gomez",
                "394485839",
                "cgomez@gmail.com",
                "+542983478634"
        );

        Specialty specialty = new Specialty(1L, "Cardiology");

        Doctor doctor = new Doctor(
                doctorId,
                "Javier",
                "Bustamante",
                "AF9082",
                specialty
        );

        when(patientRepository.findById(patientId)).thenReturn(Optional.of(patient));
        when(doctorRepository.findById(doctorId)).thenReturn(Optional.of(doctor));

        when(appointmentRepository.existsByDoctorIdAndDateTimeAndStatusNot(
                doctorId,
                futureDateTime,
                AppointmentStatus.CANCELLED
        )).thenReturn(true);

        // When & Then (Act & Assert)
        assertThrows(InvalidOperationException.class, () -> {
            appointmentService.bookAppointment(request);
        });

        verify(patientRepository, times(1)).findById(patientId);
        verify(doctorRepository, times(1)).findById(doctorId);
        verify(appointmentRepository, times(1)).existsByDoctorIdAndDateTimeAndStatusNot(
                doctorId,
                futureDateTime,
                AppointmentStatus.CANCELLED
        );
        verify(appointmentRepository, never()).save(any(Appointment.class));
    }

    @Test
    void bookAppointment_whenPatientExceedsLimit_throwsInvalidOperationException() {
        // Given (Arrange)
        Long patientId = 1L;
        Long doctorId = 1L;
        LocalDateTime futureDateTime = LocalDateTime.now().plusDays(10);

        AppointmentRequestDTO request = new AppointmentRequestDTO(
                futureDateTime,
                patientId,
                doctorId
        );

        Patient patient = new Patient(
                patientId,
                "Carlos",
                "Gomez",
                "394485839",
                "cgomez@gmail.com",
                "+542983478634"
        );

        Specialty specialty = new Specialty(1L, "Cardiology");

        Doctor doctor = new Doctor(
                doctorId,
                "Javier",
                "Bustamante",
                "AF9082",
                specialty
        );

        when(patientRepository.findById(patientId)).thenReturn(Optional.of(patient));
        when(doctorRepository.findById(doctorId)).thenReturn(Optional.of(doctor));

        when(appointmentRepository.existsByDoctorIdAndDateTimeAndStatusNot(
                doctorId,
                futureDateTime,
                AppointmentStatus.CANCELLED
        )).thenReturn(false);

        when(appointmentRepository.countByPatientIdAndStatusAndDateTimeAfter(
                eq(patientId),
                eq(AppointmentStatus.RESERVED),
                any(LocalDateTime.class)
        )).thenReturn(4);

        // When & Then (Act & Assert)
        assertThrows(InvalidOperationException.class, () -> {
            appointmentService.bookAppointment(request);
        });

        verify(patientRepository, times(1)).findById(patientId);
        verify(doctorRepository, times(1)).findById(doctorId);
        verify(appointmentRepository, times(1)).existsByDoctorIdAndDateTimeAndStatusNot(
                doctorId,
                futureDateTime,
                AppointmentStatus.CANCELLED
        );
        verify(appointmentRepository, times(1)).countByPatientIdAndStatusAndDateTimeAfter(
                eq(patientId),
                eq(AppointmentStatus.RESERVED),
                any(LocalDateTime.class)
        );
        verify(appointmentRepository, never()).save(any(Appointment.class));
    }

    @Test
    void updateAppointment_whenValidRequest_returnsAppointmentResponseDTO() {
        //Given (Arrange)
        Long patientId = 1L;
        Long appointmentId = 1L;
        Long doctorId = 1L;
        LocalDateTime futureDateTime = LocalDateTime.now().plusDays(10);

        AppointmentRequestDTO request = new AppointmentRequestDTO(
                futureDateTime,
                patientId,
                doctorId
        );

        Patient patient = new Patient(
                patientId,
                "Carlos",
                "Gomez",
                "394485839",
                "cgomez@gmail.com",
                "+542983478634"
        );

        Specialty specialty = new Specialty(1L, "Cardiology");

        Doctor doctor = new Doctor(
                doctorId,
                "Javier",
                "Bustamante",
                "AF9082",
                specialty
        );

        Appointment appointment = new Appointment(
                appointmentId,
                futureDateTime,
                AppointmentStatus.RESERVED,
                patient,
                doctor,
                LocalDateTime.now()
        );

        PatientResponseDTO patientResponse = new PatientResponseDTO(
                patientId,
                "Carlos",
                "Gomez",
                "394485839",
                "cgomez@gmail.com",
                "+542983478634"
        );

        SpecialtyResponseDTO specialtyResponse = new SpecialtyResponseDTO(1L, "Cardiology");

        DoctorResponseDTO doctorResponse = new DoctorResponseDTO(
                doctorId,
                "Javier",
                "Bustamante",
                "AF9082",
                specialtyResponse
        );

        AppointmentResponseDTO expectedResponse = new AppointmentResponseDTO(
                appointmentId,
                futureDateTime,
                AppointmentStatus.RESERVED,
                patientResponse,
                doctorResponse,
                LocalDateTime.now()
        );

        when(patientRepository.findById(patientId)).thenReturn(Optional.of(patient));
        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(appointment));
        when(doctorRepository.findById(doctorId)).thenReturn(Optional.of(doctor));
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(appointment);
        when(appointmentMapper.toResponseDTO(appointment)).thenReturn(expectedResponse);

        // When (Act)
        AppointmentResponseDTO actualResponse = appointmentService.updateAppointment(patientId, appointmentId,request);

        // Then (Assert)
        assertNotNull(actualResponse);
        assertEquals(expectedResponse.id(), actualResponse.id());
        assertEquals(expectedResponse.dateTime(), actualResponse.dateTime());

        verify(patientRepository, times(1)).findById(patientId);
        verify(appointmentRepository, times(1)).findById(appointmentId);
        verify(doctorRepository, times(1)).findById(doctorId);
        verify(appointmentRepository, times(1)).save(any(Appointment.class));
    }

    @Test
    void updateAppointment_whenPatientDoesNotExist_throwsResourceNotFoundException() {
        // Given (Arrange)
        Long patientId = 1L;
        Long appointmentId = 1L;
        Long doctorId = 1L;
        LocalDateTime futureDateTime = LocalDateTime.now().plusDays(10);

        AppointmentRequestDTO request = new AppointmentRequestDTO(
                futureDateTime,
                patientId,
                doctorId
        );

        when(patientRepository.findById(patientId)).thenReturn(Optional.empty());

        // When & Then (Act & Assert)
        assertThrows(ResourceNotFoundException.class, () -> {
            appointmentService.updateAppointment(patientId, appointmentId, request);
        });

        verify(patientRepository, times(1)).findById(patientId);
        verify(appointmentRepository, never()).save(any(Appointment.class));
    }

    @Test
    void updateAppointment_whenAppointmentDoesNotExists_throwsResourceNotFoundException() {
        // Given (Arrange)
        Long patientId = 1L;
        Long appointmentId = 1L;
        Long doctorId = 1L;
        LocalDateTime futureDateTime = LocalDateTime.now().plusDays(10);

        AppointmentRequestDTO request = new AppointmentRequestDTO(
                futureDateTime,
                patientId,
                doctorId
        );

        Patient patient = new Patient(
                patientId,
                "Carlos",
                "Gomez",
                "394485839",
                "cgomez@gmail.com",
                "+542983478634"
        );

        when(patientRepository.findById(patientId)).thenReturn(Optional.of(patient));
        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.empty());

        // When & Then (Act & Assert)
        assertThrows(ResourceNotFoundException.class, () -> {
            appointmentService.updateAppointment(patientId, appointmentId, request);
        });

        verify(patientRepository, times(1)).findById(patientId);
        verify(appointmentRepository, times(1)).findById(appointmentId);
        verify(appointmentRepository, never()).save(any(Appointment.class));
    }

    @Test
    void udpateAppointment_whenAppointment_doesNotBelongToPatient_throwsUnauthorizedOperation() {
        // Given (Arrange)
        Long patientId = 1L;
        Long appointmentId = 1L;
        Long doctorId = 1L;
        LocalDateTime futureDateTime = LocalDateTime.now().plusDays(10);

        AppointmentRequestDTO request = new AppointmentRequestDTO(
                futureDateTime,
                patientId,
                doctorId
        );

        Patient patient = new Patient(
                patientId,
                "Carlos",
                "Gomez",
                "394485839",
                "cgomez@gmail.com",
                "+542983478634"
        );

        Patient differentPatient = new Patient(
                2L,
                "Eduardo",
                "Perez",
                "32098089",
                "perez.e@gmail.com",
                "2983556345"
        );

        Specialty specialty = new Specialty(1L, "Cardiology");

        Doctor doctor = new Doctor(
                doctorId,
                "Javier",
                "Bustamante",
                "AF9082",
                specialty
        );

        Appointment appointment = new Appointment(
                appointmentId,
                futureDateTime,
                AppointmentStatus.RESERVED,
                differentPatient,
                doctor,
                LocalDateTime.now()
        );

        when(patientRepository.findById(patientId)).thenReturn(Optional.of(patient));
        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(appointment));

        // When & Then (Act & Assert)
        assertThrows(UnauthorizedOperationException.class, () -> {
            appointmentService.updateAppointment(patientId, appointmentId, request);
        });

        verify(patientRepository, times(1)).findById(patientId);
        verify(appointmentRepository, times(1)).findById(appointmentId);
        verify(appointmentRepository, never()).save(any(Appointment.class));
    }

    @Test
    void updateAppointment_whenStatusIsCancelled_throwsInvalidOperationException() {
        // Given (Arrange)
        Long patientId = 1L;
        Long appointmentId = 1L;
        Long doctorId = 1L;
        LocalDateTime futureDateTime = LocalDateTime.now().plusDays(10);

        AppointmentRequestDTO request = new AppointmentRequestDTO(
                futureDateTime,
                patientId,
                doctorId
        );

        Patient patient = new Patient(
                patientId,
                "Carlos",
                "Gomez",
                "394485839",
                "cgomez@gmail.com",
                "+542983478634"
        );

        Specialty specialty = new Specialty(1L, "Cardiology");

        Doctor doctor = new Doctor(
                doctorId,
                "Javier",
                "Bustamante",
                "AF9082",
                specialty
        );

        Appointment appointment = new Appointment(
                appointmentId,
                futureDateTime,
                AppointmentStatus.CANCELLED,
                patient,
                doctor,
                LocalDateTime.now()
        );

        when(patientRepository.findById(patientId)).thenReturn(Optional.of(patient));
        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(appointment));

        // When & Then (Act & Assert)
        assertThrows(InvalidOperationException.class, () -> {
           appointmentService.updateAppointment(patientId, appointmentId, request);
        });

        verify(patientRepository, times(1)).findById(patientId);
        verify(appointmentRepository, times(1)).findById(appointmentId);
        verify(appointmentRepository, never()).save(any(Appointment.class));
    }

    @Test
    void updateAppointment_whenStatusIsCompleted_throwsInvalidOperationException() {
        // Given (Arrange)
        Long patientId = 1L;
        Long appointmentId = 1L;
        Long doctorId = 1L;
        LocalDateTime futureDateTime = LocalDateTime.now().plusDays(10);

        AppointmentRequestDTO request = new AppointmentRequestDTO(
                futureDateTime,
                patientId,
                doctorId
        );

        Patient patient = new Patient(
                patientId,
                "Carlos",
                "Gomez",
                "394485839",
                "cgomez@gmail.com",
                "+542983478634"
        );

        Specialty specialty = new Specialty(1L, "Cardiology");

        Doctor doctor = new Doctor(
                doctorId,
                "Javier",
                "Bustamante",
                "AF9082",
                specialty
        );

        Appointment appointment = new Appointment(
                appointmentId,
                futureDateTime,
                AppointmentStatus.COMPLETED,
                patient,
                doctor,
                LocalDateTime.now()
        );

        when(patientRepository.findById(patientId)).thenReturn(Optional.of(patient));
        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(appointment));

        // When & Then (Act & Assert)
        assertThrows(InvalidOperationException.class, () -> {
            appointmentService.updateAppointment(patientId, appointmentId, request);
        });

        verify(patientRepository, times(1)).findById(patientId);
        verify(appointmentRepository, times(1)).findById(appointmentId);
        verify(appointmentRepository, never()).save(any(Appointment.class));
    }

    @Test
    void updateAppointment_whenLessThan48HoursNotice_throwsInvalidOperationException() {
        // Given (Arrange)
        Long patientId = 1L;
        Long appointmentId = 1L;
        Long doctorId = 1L;
        LocalDateTime appointmentDateTime = LocalDateTime.now().plusHours(10);
        LocalDateTime newDateTime = LocalDateTime.now().plusDays(5);

        AppointmentRequestDTO request = new AppointmentRequestDTO(
                newDateTime,
                patientId,
                doctorId
        );

        Patient patient = new Patient(
                patientId,
                "Carlos",
                "Gomez",
                "394485839",
                "cgomez@gmail.com",
                "+542983478634"
        );

        Specialty specialty = new Specialty(1L, "Cardiology");

        Doctor doctor = new Doctor(
                doctorId,
                "Javier",
                "Bustamante",
                "AF9082",
                specialty
        );

        Appointment appointment = new Appointment(
                appointmentId,
                appointmentDateTime,
                AppointmentStatus.RESERVED,
                patient,
                doctor,
                LocalDateTime.now()
        );

        when(patientRepository.findById(patientId)).thenReturn(Optional.of(patient));
        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(appointment));

        // When & Then (Act & Assert)
        assertThrows(InvalidOperationException.class, () -> {
            appointmentService.updateAppointment(patientId, appointmentId, request);
        });

        verify(patientRepository, times(1)).findById(patientId);
        verify(appointmentRepository, times(1)).findById(appointmentId);
        verify(appointmentRepository, never()).save(any(Appointment.class));
    }

    @Test
    void updateAppointment_whenDoctorDoesNotExists_throwsResourceNotFoundException() {
        // Given (Arrange)
        Long patientId = 1L;
        Long appointmentId = 1L;
        Long doctorId = 1L;
        LocalDateTime futureDateTime = LocalDateTime.now().plusDays(10);

        AppointmentRequestDTO request = new AppointmentRequestDTO(
                futureDateTime,
                patientId,
                doctorId
        );

        Patient patient = new Patient(
                patientId,
                "Carlos",
                "Gomez",
                "394485839",
                "cgomez@gmail.com",
                "+542983478634"
        );

        Specialty specialty = new Specialty(1L, "Cardiology");

        Doctor doctor = new Doctor(
                doctorId,
                "Javier",
                "Bustamante",
                "AF9082",
                specialty
        );

        Appointment appointment = new Appointment(
                appointmentId,
                futureDateTime,
                AppointmentStatus.RESERVED,
                patient,
                doctor,
                LocalDateTime.now()
        );

        when(patientRepository.findById(patientId)).thenReturn(Optional.of(patient));
        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(appointment));
        when(doctorRepository.findById(doctorId)).thenReturn(Optional.empty());

        // When & Then (Act & Assert)
        assertThrows(ResourceNotFoundException.class, () -> {
            appointmentService.updateAppointment(patientId, appointmentId, request);
        });

        verify(patientRepository, times(1)).findById(patientId);
        verify(appointmentRepository, times(1)).findById(appointmentId);
        verify(doctorRepository, times(1)).findById(doctorId);
        verify(appointmentRepository, never()).save(any(Appointment.class));
    }

    @Test
    void updateAppointment_whenDoctorIsUnavailable_throwsInvalidOperationException() {
        // Given (Arrange)
        Long patientId = 1L;
        Long appointmentId = 1L;
        Long doctorId = 1L;
        LocalDateTime futureDateTime = LocalDateTime.now().plusDays(10);

        AppointmentRequestDTO request = new AppointmentRequestDTO(
                futureDateTime,
                patientId,
                doctorId
        );

        Patient patient = new Patient(
                patientId,
                "Carlos",
                "Gomez",
                "394485839",
                "cgomez@gmail.com",
                "+542983478634"
        );

        Specialty specialty = new Specialty(1L, "Cardiology");

        Doctor doctor = new Doctor(
                doctorId,
                "Javier",
                "Bustamante",
                "AF9082",
                specialty
        );

        Appointment appointment = new Appointment(
                appointmentId,
                futureDateTime,
                AppointmentStatus.RESERVED,
                patient,
                doctor,
                LocalDateTime.now()
        );

        when(patientRepository.findById(patientId)).thenReturn(Optional.of(patient));
        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(appointment));
        when(doctorRepository.findById(doctorId)).thenReturn(Optional.of(doctor));
        when(appointmentRepository.existsByDoctorIdAndDateTimeAndStatusNotAndIdNot(
                doctorId,
                futureDateTime,
                AppointmentStatus.CANCELLED,
                appointmentId
        )).thenReturn(true);

        // When  & Then (Act & Assert)
        assertThrows(InvalidOperationException.class, () -> {
            appointmentService.updateAppointment(patientId, appointmentId, request);
        });

        verify(patientRepository, times(1)).findById(patientId);
        verify(appointmentRepository, times(1)).findById(appointmentId);
        verify(doctorRepository, times(1)).findById(doctorId);
        verify(appointmentRepository, times(1)).existsByDoctorIdAndDateTimeAndStatusNotAndIdNot(
                doctorId,
                futureDateTime,
                AppointmentStatus.CANCELLED,
                appointmentId
        );
        verify(appointmentRepository, never()).save(any(Appointment.class));
    }

    @Test
    void updateAppointment_whenDateTimeIsPast_throwsInvalidOperationException() {
        // Given (Arrange)
        Long patientId = 1L;
        Long appointmentId = 1L;
        Long doctorId = 1L;
        LocalDateTime currentAppointmentDateTime = LocalDateTime.now().plusDays(10);
        LocalDateTime pastDateTime = LocalDateTime.now().minusDays(10);
        LocalDateTime now = LocalDateTime.now();

        AppointmentRequestDTO request = new AppointmentRequestDTO(
                pastDateTime,
                patientId,
                doctorId
        );

        Patient patient = new Patient(
                patientId,
                "Carlos",
                "Gomez",
                "394485839",
                "cgomez@gmail.com",
                "+542983478634"
        );

        Specialty specialty = new Specialty(1L, "Cardiology");

        Doctor doctor = new Doctor(
                doctorId,
                "Javier",
                "Bustamante",
                "AF9082",
                specialty
        );

        Appointment appointment = new Appointment(
                appointmentId,
                currentAppointmentDateTime,
                AppointmentStatus.RESERVED,
                patient,
                doctor,
                LocalDateTime.now()
        );

        when(patientRepository.findById(patientId)).thenReturn(Optional.of(patient));
        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(appointment));
        when(doctorRepository.findById(doctorId)).thenReturn(Optional.of(doctor));
        when(appointmentRepository.existsByDoctorIdAndDateTimeAndStatusNotAndIdNot(
                doctorId,
                pastDateTime,
                AppointmentStatus.CANCELLED,
                appointmentId
        )).thenReturn(false);

        // When & Then (Act & Assert)
        assertThrows(InvalidOperationException.class, () -> {
            appointmentService.updateAppointment(patientId, appointmentId, request);
        });

        verify(patientRepository, times(1)).findById(patientId);
        verify(appointmentRepository, times(1)).findById(appointmentId);
        verify(doctorRepository, times(1)).findById(doctorId);
        verify(appointmentRepository, times(1)).existsByDoctorIdAndDateTimeAndStatusNotAndIdNot(
                doctorId,
                pastDateTime,
                AppointmentStatus.CANCELLED,
                appointmentId
        );
        verify(appointmentRepository, never()).save(any(Appointment.class));
    }

    @Test
    void completeAppointment_whenValidRequest_returnsAppointmentResponseDTO() {
        // Given (Arrange)
        Long appointmentId = 1L;
        LocalDateTime currentAppointmentDateTime = LocalDateTime.now();

        Patient patient = new Patient(
               1L,
                "Carlos",
                "Gomez",
                "394485839",
                "cgomez@gmail.com",
                "+542983478634"
        );

        Specialty specialty = new Specialty(1L, "Cardiology");

        Doctor doctor = new Doctor(
                1L,
                "Javier",
                "Bustamante",
                "AF9082",
                specialty
        );

        Appointment appointment = new Appointment(
                appointmentId,
                currentAppointmentDateTime,
                AppointmentStatus.RESERVED,
                patient,
                doctor,
                LocalDateTime.now()
        );

        PatientResponseDTO patientResponse = new PatientResponseDTO(
                1L,
                "Carlos",
                "Gomez",
                "394485839",
                "cgomez@gmail.com",
                "+542983478634"
        );

        SpecialtyResponseDTO specialtyResponse = new SpecialtyResponseDTO(1L, "Cardiology");

        DoctorResponseDTO doctorResponse = new DoctorResponseDTO(
                1L,
                "Javier",
                "Bustamante",
                "AF9082",
                specialtyResponse
        );

        AppointmentResponseDTO expectedResponse = new AppointmentResponseDTO(
                appointmentId,
                currentAppointmentDateTime,
                AppointmentStatus.COMPLETED,
                patientResponse,
                doctorResponse,
                LocalDateTime.now()
        );


        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(appointment));
        when(appointmentRepository.save(appointment)).thenReturn(appointment);
        when(appointmentMapper.toResponseDTO(appointment)).thenReturn(expectedResponse);

        // When (Act)
        AppointmentResponseDTO actualResponse = appointmentService.completeAppointment(appointmentId);

        // Then (Assert)
        assertNotNull(actualResponse);
        assertEquals(actualResponse.id(), expectedResponse.id());
        assertEquals(actualResponse.dateTime(), expectedResponse.dateTime());

        verify(appointmentRepository, times(1)).findById(appointmentId);
        verify(appointmentRepository, times(1)).save(appointment);
        verify(appointmentMapper, times(1)).toResponseDTO(appointment);
    }

    @Test
    void completeAppointment_whenAppointmentDoesNotExists_throwsResourceNotFoundException() {
        // Given (Arrange)
        Long appointmentId = 1L;

        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.empty());

        // When & Then (Act & Assert)
        assertThrows(ResourceNotFoundException.class, () -> {
            appointmentService.completeAppointment(appointmentId);
        });

        verify(appointmentRepository, times(1)).findById(appointmentId);
        verify(appointmentRepository, never()).save(any(Appointment.class));
    }

    @Test
    void completeAppointment_whenStatusIsNotReserved_throwsInvalidOperationException() {
        // Given (Arrange)
        Long appointmentId = 1L;

        Appointment appointment = new Appointment();
        appointment.setStatus(AppointmentStatus.CANCELLED);

        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(appointment));

        // When & Then (Act & Assert)
        assertThrows(InvalidOperationException.class, () -> {
            appointmentService.completeAppointment(appointmentId);
        });

        verify(appointmentRepository, times(1)).findById(appointmentId);
        verify(appointmentRepository, never()).save(any(Appointment.class));
    }

    @Test
    void completeAppointment_whenBeforeWindowStart_throwsInvalidOperationException() {
        // Given (Arrange)
        Long appointmentId = 1L;

        LocalDateTime appointmentDateTime = LocalDateTime.now().plusHours(2);

        Appointment appointment = new Appointment();
        appointment.setDateTime(appointmentDateTime);
        appointment.setStatus(AppointmentStatus.RESERVED);

        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(appointment));

        // When & Then (Act & Assert)
        assertThrows(InvalidOperationException.class, () -> {
            appointmentService.completeAppointment(appointmentId);
        });

        verify(appointmentRepository, times(1)).findById(appointmentId);
        verify(appointmentRepository, never()).save(any(Appointment.class));
    }

    @Test
    void completeAppointment_whenAfterWindowEnd_throwsInvalidOperationException() {
        // Given (Arrange)
        Long appointmentId = 1L;

        LocalDateTime appointmentDateTime = LocalDateTime.now().minusMinutes(15);

        Appointment appointment = new Appointment();
        appointment.setDateTime(appointmentDateTime);
        appointment.setStatus(AppointmentStatus.RESERVED);

        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(appointment));

        // When & Then (Act & Assert)
        InvalidOperationException exception = assertThrows(InvalidOperationException.class, () -> {
            appointmentService.completeAppointment(appointmentId);
        });

        assertEquals("El turno ha expirado", exception.getMessage());

        verify(appointmentRepository, times(1)).findById(appointmentId);
        verify(appointmentRepository, never()).save(any(Appointment.class));
    }

    @Test
    void cancelAppointment_whenValidRequest_returnsAppointmentResponseDTO() {
        // Given (Arrange)
        Long appointmentId = 1L;
        Long patientId = 1L;
        LocalDateTime currentAppointmentDateTime = LocalDateTime.now().plusDays(10);

        Patient patient = new Patient(
                patientId,
                "Carlos",
                "Gomez",
                "394485839",
                "cgomez@gmail.com",
                "+542983478634"
        );

        Specialty specialty = new Specialty(1L, "Cardiology");

        Doctor doctor = new Doctor(
                1L,
                "Javier",
                "Bustamante",
                "AF9082",
                specialty
        );

        Appointment appointment = new Appointment(
                appointmentId,
                currentAppointmentDateTime,
                AppointmentStatus.RESERVED,
                patient,
                doctor,
                LocalDateTime.now()
        );

        PatientResponseDTO patientResponse = new PatientResponseDTO(
                1L,
                "Carlos",
                "Gomez",
                "394485839",
                "cgomez@gmail.com",
                "+542983478634"
        );

        SpecialtyResponseDTO specialtyResponse = new SpecialtyResponseDTO(1L, "Cardiology");

        DoctorResponseDTO doctorResponse = new DoctorResponseDTO(
                1L,
                "Javier",
                "Bustamante",
                "AF9082",
                specialtyResponse
        );

        AppointmentResponseDTO expectedResponse = new AppointmentResponseDTO(
                appointmentId,
                currentAppointmentDateTime,
                AppointmentStatus.CANCELLED,
                patientResponse,
                doctorResponse,
                LocalDateTime.now()
        );

        when(patientRepository.findById(patientId)).thenReturn(Optional.of(patient));
        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(appointment));
        when(appointmentRepository.save(appointment)).thenReturn(appointment);
        when(appointmentMapper.toResponseDTO(appointment)).thenReturn(expectedResponse);

        // When (Act)
        AppointmentResponseDTO actualResponse = appointmentService.cancelAppointment(patientId, appointmentId);

        // Then (Assert)
        assertNotNull(actualResponse);
        assertEquals(actualResponse.id(), expectedResponse.id());
        assertEquals(actualResponse.dateTime(), expectedResponse.dateTime());

        verify(patientRepository, times(1)).findById(patientId);
        verify(appointmentRepository, times(1)).findById(appointmentId);
        verify(appointmentRepository, times(1)).save(any(Appointment.class));
        verify(appointmentMapper, times(1)).toResponseDTO(any(Appointment.class));
    }

    @Test
    void cancelAppointment_whenPatientDoesNotExists_throwsResourceNotFoundException() {
        // Given (Arrange)
        Long patientId = 1L;
        Long appointmentId = 1L;

        when(patientRepository.findById(patientId)).thenReturn(Optional.empty());

        // When & Then (Act & Assert)
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            appointmentService.cancelAppointment(patientId, appointmentId);
        });

        assertNotNull(exception);
        assertEquals("Paciente no encontrado", exception.getMessage());

        verify(patientRepository, times(1)).findById(patientId);
        verify(appointmentRepository, never()).save(any(Appointment.class));
    }

    @Test
    void cancelAppointment_whenAppointmentDoesNotExists_throwsResourceNotFoundException() {
        // Given (Arrange)
        Long patientId = 1L;
        Long appointmentId = 1L;

        Patient patient = new Patient();

        when(patientRepository.findById(patientId)).thenReturn(Optional.of(patient));
        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.empty());

        // When & Then (Act & Assert)
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            appointmentService.cancelAppointment(patientId, appointmentId);
        });

        assertNotNull(exception);
        assertEquals("Turno no encontrado", exception.getMessage());

        verify(patientRepository, times(1)).findById(patientId);
        verify(appointmentRepository, times(1)).findById(appointmentId);
        verify(appointmentRepository, never()).save(any(Appointment.class));
    }

    @Test
    void cancelAppointment_whenAppointmentDoesNotBelongToPatient_throwsUnauthorizedOperationException() {
        // Given (Arrange)
        Long patientId = 1L;
        Long appointmentId = 1L;
        LocalDateTime appointmentLocalDateTime = LocalDateTime.now().plusDays(10);

        Patient patient = new Patient(
                2L,
                "Carlos",
                "Gomez",
                "394485839",
                "cgomez@gmail.com",
                "+542983478634"
        );

        Specialty specialty = new Specialty(1L, "Cardiology");

        Doctor doctor = new Doctor(
                1L,
                "Javier",
                "Bustamante",
                "AF9082",
                specialty
        );

        Appointment appointment = new Appointment(
                appointmentId,
                appointmentLocalDateTime,
                AppointmentStatus.RESERVED,
                patient,
                doctor,
                LocalDateTime.now()
        );

        when(patientRepository.findById(patientId)).thenReturn(Optional.of(patient));
        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(appointment));

        // When & Then (Act & Assert)
        UnauthorizedOperationException exception = assertThrows(UnauthorizedOperationException.class, () -> {
            appointmentService.cancelAppointment(patientId, appointmentId);
        });

        assertNotNull(exception);
        assertEquals("No existe un turno asignado con ese ID", exception.getMessage());

        verify(patientRepository, times(1)).findById(patientId);
        verify(appointmentRepository, times(1)).findById(appointmentId);
        verify(appointmentRepository, never()).save(any(Appointment.class));
    }

    @Test
    void cancelAppointment_whenStatusIsAlreadyCancelled_throwsInvalidOperationException() {
        // Given (Assert)
        Long patientId = 1L;
        Long appointmentId = 1L;
        LocalDateTime appointmentLocalDateTime = LocalDateTime.now().plusDays(10);

        Patient patient = new Patient(
                patientId,
                "Carlos",
                "Gomez",
                "394485839",
                "cgomez@gmail.com",
                "+542983478634"
        );

        Specialty specialty = new Specialty(1L, "Cardiology");

        Doctor doctor = new Doctor(
                1L,
                "Javier",
                "Bustamante",
                "AF9082",
                specialty
        );

        Appointment appointment = new Appointment(
                appointmentId,
                appointmentLocalDateTime,
                AppointmentStatus.CANCELLED,
                patient,
                doctor,
                LocalDateTime.now()
        );

        when(patientRepository.findById(patientId)).thenReturn(Optional.of(patient));
        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(appointment));

        // When & Then (Act & Assert)
        InvalidOperationException exception = assertThrows(InvalidOperationException.class, () -> {
            appointmentService.cancelAppointment(patientId, appointmentId);
        });

        assertNotNull(exception);
        assertEquals("El turno ya fue cancelado", exception.getMessage());

        verify(patientRepository, times(1)).findById(patientId);
        verify(appointmentRepository, times(1)).findById(appointmentId);
        verify(appointmentRepository, never()).save(any(Appointment.class));
    }

    @Test
    void cancelAppointment_whenStatusIsAlreadyCompleted_throwsInvalidOperationException() {
        // Given (Assert)
        Long patientId = 1L;
        Long appointmentId = 1L;
        LocalDateTime appointmentLocalDateTime = LocalDateTime.now().plusDays(10);

        Patient patient = new Patient(
                patientId,
                "Carlos",
                "Gomez",
                "394485839",
                "cgomez@gmail.com",
                "+542983478634"
        );

        Specialty specialty = new Specialty(1L, "Cardiology");

        Doctor doctor = new Doctor(
                1L,
                "Javier",
                "Bustamante",
                "AF9082",
                specialty
        );

        Appointment appointment = new Appointment(
                appointmentId,
                appointmentLocalDateTime,
                AppointmentStatus.COMPLETED,
                patient,
                doctor,
                LocalDateTime.now()
        );

        when(patientRepository.findById(patientId)).thenReturn(Optional.of(patient));
        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(appointment));

        // When & Then (Act & Assert)
        InvalidOperationException exception = assertThrows(InvalidOperationException.class, () -> {
            appointmentService.cancelAppointment(patientId, appointmentId);
        });

        assertNotNull(exception);
        assertEquals("El turno ya ha sido completado", exception.getMessage());

        verify(patientRepository, times(1)).findById(patientId);
        verify(appointmentRepository, times(1)).findById(appointmentId);
        verify(appointmentRepository, never()).save(any(Appointment.class));
    }

    @Test
    void cancelAppointment_whenLessThan48HoursNotice_throwsInvalidOperationException() {
        // Given (Assert)
        Long patientId = 1L;
        Long appointmentId = 1L;
        LocalDateTime appointmentLocalDateTime = LocalDateTime.now().plusHours(24);

        Patient patient = new Patient(
                patientId,
                "Carlos",
                "Gomez",
                "394485839",
                "cgomez@gmail.com",
                "+542983478634"
        );

        Specialty specialty = new Specialty(1L, "Cardiology");

        Doctor doctor = new Doctor(
                1L,
                "Javier",
                "Bustamante",
                "AF9082",
                specialty
        );

        Appointment appointment = new Appointment(
                appointmentId,
                appointmentLocalDateTime,
                AppointmentStatus.RESERVED,
                patient,
                doctor,
                LocalDateTime.now()
        );

        when(patientRepository.findById(patientId)).thenReturn(Optional.of(patient));
        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(appointment));

        // When & Then (Act & Assert)
        InvalidOperationException exception = assertThrows(InvalidOperationException.class, () -> {
            appointmentService.cancelAppointment(patientId, appointmentId);
        });

        assertNotNull(exception);
        assertEquals(
                "No se puede cancelar un turno con menos de 48 hs de anticipación",
                exception.getMessage()
        );

        verify(patientRepository, times(1)).findById(patientId);
        verify(appointmentRepository, times(1)).findById(appointmentId);
        verify(appointmentRepository, never()).save(any(Appointment.class));
      }
    }
