package com.manuelfoulkes.turnos_medicos.services;

import com.manuelfoulkes.turnos_medicos.dtos.requests.DoctorRequestDTO;
import com.manuelfoulkes.turnos_medicos.dtos.responses.DoctorResponseDTO;
import com.manuelfoulkes.turnos_medicos.dtos.responses.SpecialtyResponseDTO;
import com.manuelfoulkes.turnos_medicos.entities.Doctor;
import com.manuelfoulkes.turnos_medicos.entities.Specialty;
import com.manuelfoulkes.turnos_medicos.exceptions.custom.ResourceAlreadyExistsException;
import com.manuelfoulkes.turnos_medicos.exceptions.custom.ResourceNotFoundException;
import com.manuelfoulkes.turnos_medicos.mappers.DoctorMapper;
import com.manuelfoulkes.turnos_medicos.repositories.DoctorRepository;
import com.manuelfoulkes.turnos_medicos.repositories.SpecialtyRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DoctorServiceTest {

    @Mock
    DoctorRepository doctorRepository;

    @Mock
    SpecialtyRepository specialtyRepository;

    @Mock
    DoctorMapper doctorMapper;

    @InjectMocks
    DoctorService doctorService;

    @Test
    void getDoctorById_whenDoctorExists_returnsDoctorResponseDTO() {
        // Given (Arrange)
        Long id = 1L;

        Specialty specialty = new Specialty(1L, "Cardiology");

        SpecialtyResponseDTO specialtyResponse = new SpecialtyResponseDTO(1L, "Cardiology");

        Doctor doctor = new Doctor(
                1L,
                "Horacio",
                "Martinez",
                "AB9283",
                specialty
        );

        DoctorResponseDTO expectedResponse = new DoctorResponseDTO(
                1L,
                "Horacio",
                "Martinez",
                "AB9283",
                specialtyResponse
        );

        when(doctorRepository.findById(id)).thenReturn(Optional.of(doctor));
        when(doctorMapper.toResponseDTO(doctor)).thenReturn(expectedResponse);

        // When (Act)
        DoctorResponseDTO actualResponse = doctorService.getDoctorById(id);

        // Then (Assert)
        assertNotNull(actualResponse);
        assertEquals(actualResponse.name(), expectedResponse.name());

        verify(doctorRepository, times(1)).findById(id);
        verify(doctorMapper, times(1)).toResponseDTO(doctor);
    }

    @Test
    void getDoctorById_whenDoctorDoesNotExist_throwsResourceNotFoundException() {
        // Given (Arrange)
        Long id = 1L;

        when(doctorRepository.findById(id)).thenReturn(Optional.empty());

        // When & Then (Act & Assert)
        assertThrows(ResourceNotFoundException.class, () -> {
           doctorService.getDoctorById(id);
        });

        verify(doctorRepository, times(1)).findById(id);
        verify(doctorRepository, never()).save(any(Doctor.class));
    }

    @Test
    void getAllDoctors_whenDoctorExists_returnsDoctorsResponseDTOList() {
        // Given (Arrange)
        Specialty specialty = new Specialty(1L, "Cardiology");
        Doctor doctor1 = new Doctor(
                1L,
                "Horacio",
                "Martinez",
                "AB9283",
                specialty
        );
        Doctor doctor2 = new Doctor(
                2L,
                "Javier",
                "Bustamante",
                "AF9082",
                specialty
        );
        List<Doctor> doctors = List.of(doctor1, doctor2);
        SpecialtyResponseDTO specialtyResponse = new SpecialtyResponseDTO(1L, "Cardiology");
        DoctorResponseDTO dto1 = new DoctorResponseDTO(
                1L,
                "Horacio",
                "Martinez",
                "AB9283",
                specialtyResponse
        );
        DoctorResponseDTO dto2 = new DoctorResponseDTO(
                2L,
                "Javier",
                "Bustamante",
                "AF9082",
                specialtyResponse
        );
        List<DoctorResponseDTO> expectedResponseList = List.of(dto1, dto2);

        when(doctorRepository.findAll()).thenReturn(doctors);
        when(doctorMapper.toResponseDTO(doctor1)).thenReturn(dto1);
        when(doctorMapper.toResponseDTO(doctor2)).thenReturn(dto2);

        // When (Act)
        List<DoctorResponseDTO> actualResponseList = doctorService.getAllDoctors();

        // Then (Assert)
        assertNotNull(actualResponseList);
        assertEquals(actualResponseList.size(), expectedResponseList.size());
        assertEquals(actualResponseList.get(0).name(), expectedResponseList.get(0).name());

        verify(doctorRepository, times(1)).findAll();
        verify(doctorMapper, times(2)).toResponseDTO(any(Doctor.class));
    }

    @Test
    void getAllDoctors_whenDoctorsDoesNotExists_returnsEmptyList() {
        // Given (Arrange)
        when(doctorRepository.findAll()).thenReturn(Collections.emptyList());

        // When (Act)
        List<DoctorResponseDTO> doctorResponseList = doctorService.getAllDoctors();

        // Then (Assert)
        assertNotNull(doctorResponseList);
        assertTrue(doctorResponseList.isEmpty());
        verify(doctorRepository, times(1)).findAll();
    }

    @Test
    void createDoctor_whenRequestIsValid_returnsDoctorResponseDTO() {
        // Given (Arrange)
        Long doctorId = 1L;
        Long specialtyId = 1L;
        String licenseNumber = "AF9082";

        DoctorRequestDTO request = new DoctorRequestDTO(
                "Javier",
                "Bustamante",
                licenseNumber,
                specialtyId
        );

        Specialty specialty = new Specialty(specialtyId, "Cardiology");

        Doctor doctor = new Doctor(
                doctorId,
                "Javier",
                "Bustamante",
                licenseNumber,
                specialty
        );

        SpecialtyResponseDTO specialtyResponse = new SpecialtyResponseDTO(1L, "Cardiology");

        DoctorResponseDTO expectedResponse = new DoctorResponseDTO(
                doctorId,
                "Javier",
                "Bustamante",
                licenseNumber,
                specialtyResponse
        );

        when(doctorRepository.findByLicenseNumber(licenseNumber)).thenReturn(Optional.empty());
        when(specialtyRepository.findById(specialtyId)).thenReturn(Optional.of(specialty));
        when(doctorMapper.toEntity(request)).thenReturn(doctor);
        when(doctorRepository.save(doctor)).thenReturn(doctor);
        when(doctorMapper.toResponseDTO(doctor)).thenReturn(expectedResponse);

        // When (Act)
        DoctorResponseDTO actualResponse = doctorService.createDoctor(request);

        // Then (Assert)
        assertNotNull(actualResponse);
        assertEquals(actualResponse.name(), expectedResponse.name());
        verify(doctorRepository, times(1)).findByLicenseNumber(licenseNumber);
        verify(specialtyRepository, times(1)).findById(specialtyId);
        verify(doctorMapper, times(1)).toEntity(request);
        verify(doctorRepository, times(1)).save(doctor);
        verify(doctorMapper, times(1)).toResponseDTO(any(Doctor.class));
    }

    @Test
    void createDoctor_whenDoctorAlreadyExists_throwsResourceAlreadyExistsException() {
        // Given (Arrange)
        Long doctorId = 1L;
        Long specialtyId = 1L;
        String licenseNumber = "AF9082";

        Specialty specialty = new Specialty(specialtyId, "Cardiology");

        Doctor doctor = new Doctor(
                specialtyId,
                "Javier",
                "Bustamante",
                licenseNumber,
                specialty
        );

        DoctorRequestDTO request = new DoctorRequestDTO(
                "Javier",
                "Bustamante",
                licenseNumber,
                specialtyId
        );

        when(doctorRepository.findByLicenseNumber(licenseNumber)).thenReturn(Optional.of(doctor));

        // When & Then (Act & Assert)
        assertThrows(ResourceAlreadyExistsException.class, () -> {
            doctorService.createDoctor(request);
        });

        verify(doctorRepository, times(1)).findByLicenseNumber(licenseNumber);
        verify(doctorRepository, never()).save(doctor);
    }

    @Test
    void createDoctor_whenSpecialtyDoesNotExists_throwsResourceNotFoundException() {
        // Given (Arrange)
        String licenseNumber = "AF9082";

        DoctorRequestDTO request = new DoctorRequestDTO(
                "Javier",
                "Bustamante",
                licenseNumber,
                1L
        );

        when(doctorRepository.findByLicenseNumber(licenseNumber)).thenReturn(Optional.empty());

        // When & Then (Act & Assert)
        assertThrows(ResourceNotFoundException.class, () -> {
            doctorService.createDoctor(request);
        });

        verify(doctorRepository, times(1)).findByLicenseNumber(licenseNumber);
        verify(doctorRepository, never()).save(any(Doctor.class));
    }

    @Test
    void updateDoctor_whenRequestIsValid_returnsDoctorResponseDTO() {
        // Given (Arrange)
        Long doctorId = 1L;
        Long specialtyId = 1L;
        String licenseNumber = "AF9082";

        DoctorRequestDTO request = new DoctorRequestDTO(
                "Javier",
                "Bustamante",
                licenseNumber,
                doctorId
        );

        Specialty specialty = new Specialty(specialtyId, "Cardiology");

        Doctor doctor = new Doctor(
                doctorId,
                "Javier",
                "Bustamante",
                licenseNumber,
                specialty
        );

        SpecialtyResponseDTO specialtyResponse = new SpecialtyResponseDTO(specialtyId, "Cardiology");

        DoctorResponseDTO expectedResponse = new DoctorResponseDTO(
                doctorId,
                "Javier",
                "Bustamante",
                licenseNumber,
                specialtyResponse
        );

        when(doctorRepository.findById(doctorId)).thenReturn(Optional.of(doctor));
        when(specialtyRepository.findById(specialtyId)).thenReturn(Optional.of(specialty));
        when(doctorRepository.save(doctor)).thenReturn(doctor);
        when(doctorMapper.toResponseDTO(doctor)).thenReturn(expectedResponse);

        // When (Act)
        DoctorResponseDTO actualResponse = doctorService.updateDoctor(doctorId, request);

        // Then (Assert)
        assertNotNull(actualResponse);
        assertEquals(actualResponse.name(), expectedResponse.name());

        verify(doctorRepository, times(1)).findById(doctorId);
        verify(specialtyRepository, times(1)).findById(specialtyId);
        verify(doctorRepository, times(1)).save(doctor);
        verify(doctorMapper, times(1)).toResponseDTO(doctor);
    }

    @Test
    void updateDoctor_whenDoctorDoesNotExist_throwsResourceNotFoundException() {
        // Given (Arrange)
        Long doctorId = 1L;

        DoctorRequestDTO request = new DoctorRequestDTO(
                "Javier",
                "Bustamante",
                "AF9082",
                1L
        );
        when(doctorRepository.findById(doctorId)).thenReturn(Optional.empty());

        // When & Then (Act & Assert)
        assertThrows(ResourceNotFoundException.class, () -> {
            doctorService.updateDoctor(doctorId, request);
        });

        verify(doctorRepository, times(1)).findById(doctorId);
        verify(doctorRepository, never()).save(any(Doctor.class));
    }

    @Test
    void updateDoctor_whenSpecialtyDoesNotExists_throwsResourceNotFoundException() {
        // Given (Arrange)
        Long doctorId = 1L;
        Long specialtyId = 1L;

        DoctorRequestDTO request = new DoctorRequestDTO(
                "Javier",
                "Bustamante",
                "AF9082",
                specialtyId
        );

        Specialty specialty = new Specialty(1L, "Cardiology");

        Doctor doctor = new Doctor(
                1L,
                "Javier",
                "Bustamante",
                "AF9082",
                specialty
        );

        when(doctorRepository.findById(doctorId)).thenReturn(Optional.of(doctor));
        when(specialtyRepository.findById(specialtyId)).thenReturn(Optional.empty());

        // When & Then (Act & Assert)
        assertThrows(ResourceNotFoundException.class, () -> {
            doctorService.updateDoctor(doctorId, request);
        });

        verify(doctorRepository, times(1)).findById(doctorId);
        verify(specialtyRepository, times(1)).findById(specialtyId);
        verify(doctorRepository, never()).save(any(Doctor.class));
    }

    @Test
    void deleteDoctor_whenDoctorExists_deletesDoctor() {
        // Given (Arrange)
        Long  doctorId = 1L;

        Specialty specialty = new Specialty(1L, "Cardiology");

        Doctor doctor = new Doctor(
                1L,
                "Javier",
                "Bustamante",
                "AF9082",
                specialty
        );

        when(doctorRepository.findById(doctorId)).thenReturn(Optional.of(doctor));

        // When (Act)
        doctorService.deleteDoctor(doctorId);

        // Then (Assert)
        verify(doctorRepository, times(1)).findById(doctorId);
        verify(doctorRepository, times(1)).delete(doctor);
    }

    @Test
    void deleteDoctor_whenDoctorDoesNotExist_throwsResourceNotFoundException() {
        // Given (Arrange)
        Long doctorId = 1L;

        when(doctorRepository.findById(doctorId)).thenReturn(Optional.empty());

        // When & Then (Act & Assert)
        assertThrows(ResourceNotFoundException.class, () -> {
            doctorService.deleteDoctor(doctorId);
        });

        verify(doctorRepository, times(1)).findById(doctorId);
        verify(doctorRepository, never()).delete(any(Doctor.class));
    }
}
