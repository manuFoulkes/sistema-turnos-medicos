package com.manuelfoulkes.turnos_medicos.services;

import com.manuelfoulkes.turnos_medicos.dtos.requests.PatientRequestDTO;
import com.manuelfoulkes.turnos_medicos.dtos.responses.PatientResponseDTO;
import com.manuelfoulkes.turnos_medicos.entities.Patient;
import com.manuelfoulkes.turnos_medicos.exceptions.custom.ResourceAlreadyExistsException;
import com.manuelfoulkes.turnos_medicos.exceptions.custom.ResourceNotFoundException;
import com.manuelfoulkes.turnos_medicos.mappers.PatientMapper;
import com.manuelfoulkes.turnos_medicos.repositories.PatientRepository;
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
public class PatientServiceTest {

    @Mock
    PatientRepository patientRepository;

    @Mock
    PatientMapper patientMapper;

    @InjectMocks
    PatientService patientService;

    @Test
    void  getById_whenPatientExists_returnPatientResponseDTO() {
        // Given (Arrange)
        Long patientId = 1L;

        Patient patient = new Patient(
                patientId,
                "Carlos",
                "Gomez",
                "394485839",
                "cgomez@gmail.com",
                "+542983478634"
        );

        PatientResponseDTO expectedResponse = new PatientResponseDTO(
                patientId,
                "Carlos",
                "Gomez",
                "394485839",
                "cgomez@gmail.com",
                "542983478634"
        );

        when(patientRepository.findById(patientId)).thenReturn(Optional.of(patient));
        when(patientMapper.toResponseDTO(patient)).thenReturn(expectedResponse);

        // When (Act)
        PatientResponseDTO actualResponse = patientService.getById(patientId);

        // Then (Assert)
        assertNotNull(actualResponse);
        assertEquals(actualResponse.name(), expectedResponse.name());

        verify(patientRepository, times(1)).findById(patientId);
        verify(patientMapper, times(1)).toResponseDTO(patient);
    }

    @Test
    void getById_whenPatientDoesNotExists_throwsResourceNotFoundException() {
        // Given (Arrange)
        Long id = 1L;
        when(patientRepository.findById(id)).thenReturn(Optional.empty());

        // When & Then (Act & Assert)
        assertThrows(ResourceNotFoundException.class, () -> {
            patientService.getById(id);
        });

        verify(patientRepository, times(1)).findById(id);
        verify(patientRepository, never()).save(any(Patient.class));
    }

    @Test
    void getAllPatients_whenPatientExists_returnPatientResponseDTOList() {
        // Given (Arrange)
        Patient patient1 = new Patient(
                1L,
                "Carlos",
                "Gomez",
                "394485839",
                "cgomez@gmail.com",
                "+542983478634"
        );

        Patient patient2 = new Patient(
                2L,
                "Juan",
                "Rodriguez",
                "786675765",
                "jrodriguez@gmail.com",
                "+542983567269"
        );
        List<Patient> patients = List.of(patient1, patient2);

        PatientResponseDTO dto1 = new PatientResponseDTO(
                1L,
                "Carlos",
                "Gomez",
                "394485839",
                "cgomez@gmail.com",
                "542983478634"
        );

        PatientResponseDTO dto2 = new PatientResponseDTO(
                2L,
                "Juan",
                "Rodriguez",
                "786675765",
                "jrodriguez@gmail.com",
                "+542983567269"
        );

        when(patientRepository.findAll()).thenReturn(patients);
        when(patientMapper.toResponseDTO(patient1)).thenReturn(dto1);
        when(patientMapper.toResponseDTO(patient2)).thenReturn(dto2);

        // When (Act)
        List<PatientResponseDTO> result = patientService.getAllPatients();

        // Then (Assert)
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(result.get(0).name(), dto1.name());
        assertEquals(result.get(1).name(), dto2.name());

        verify(patientRepository, times(1)).findAll();
        verify(patientMapper, times(2)).toResponseDTO(any(Patient.class));
    }

    @Test
    void getAllPatients_whenPatientsDoesNotExists_returnsAnEmptyList() {
        // Given (Arrange)
        when(patientRepository.findAll()).thenReturn(Collections.emptyList());

        // When (Act)
        List<PatientResponseDTO> result = patientService.getAllPatients();

        // Then (Assert)
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(patientRepository, times(1)).findAll();
    }

    @Test
    void createPatient_whenRequestIsValid_returnsPatientResponseDTO() {
        // Given (Arrange)
        Long patientId = 1L;
        String nationalId = "394485839";

        Patient patient = new Patient(
                patientId,
                "Carlos",
                "Gomez",
                nationalId,
                "cgomez@gmail.com",
                "542983478634"
        );

        PatientRequestDTO request = new PatientRequestDTO(
                "Carlos",
                "Gomez",
                nationalId,
                "cgomez@gmail.com",
                "542983478634"
        );

        PatientResponseDTO expectedResponse = new PatientResponseDTO(
                patientId,
                "Carlos",
                "Gomez",
                nationalId,
                "cgomez@gmail.com",
                "542983478634"
        );

        // When (Act)
        when(patientRepository.findByNationalId(nationalId)).thenReturn(Optional.empty());
        when(patientMapper.toEntity(request)).thenReturn(patient);
        when(patientRepository.save(patient)).thenReturn(patient);
        when(patientMapper.toResponseDTO(patient)).thenReturn(expectedResponse);

        PatientResponseDTO actualResponse = patientService.createPatient(request);

        // Then (Assert)
        assertNotNull(actualResponse);
        assertEquals(expectedResponse.id(), actualResponse.id());
        assertEquals(expectedResponse.name(), actualResponse.name());

        verify(patientRepository, times(1)).findByNationalId(nationalId);
        verify(patientMapper, times(1)).toEntity(request);
        verify(patientRepository, times(1)).save(patient);
        verify(patientMapper, times(1)).toResponseDTO(patient);
    }

    @Test
    void createPatient_whenPatientAlreadyExists_throwsResourceAlreadyExistsException() {
        // Given (Arrange)
        String nationalId = "394485839";

        Patient patient = new Patient(
                1L,
                "Carlos",
                "Gomez",
                nationalId,
                "cgomez@gmail.com",
                "542983478634"
        );

        PatientRequestDTO request = new PatientRequestDTO(
                "Carlos",
                "Gomez",
                nationalId,
                "cgomez@gmail.com",
                "542983478634"
        );

        when(patientRepository.findByNationalId(nationalId)).thenReturn(Optional.of(patient));

        // When (Act)
        assertThrows(ResourceAlreadyExistsException.class, () -> {
            patientService.createPatient(request);
        });

        // Then (Assert)
        verify(patientRepository, times(1)).findByNationalId(nationalId);
        verify(patientRepository, never()).save(patient);
    }

    @Test
    void updatePatient_whenRequestIsValid_updatesAndReturnsPatientResponseDTO() {
        // Given (Arrange)
        Long patientId = 1L;

        Patient patient = new Patient(
                patientId,
                "Carlos",
                "Gomez",
                "394485839",
                "cgomez@gmail.com",
                "542983478634"
        );

        PatientRequestDTO request = new PatientRequestDTO(
                "Carlos",
                "Gomez",
                "394485839",
                "cgomez@gmail.com",
                "542983478634"
        );

        PatientResponseDTO expectedResponse = new PatientResponseDTO(
                patientId,
                "Carlos",
                "Gomez",
                "394485839",
                "cgomez@gmail.com",
                "542983478634"
        );

        when(patientRepository.findById(patientId)).thenReturn(Optional.of(patient));
        when(patientRepository.save(patient)).thenReturn(patient);
        when(patientMapper.toResponseDTO(patient)).thenReturn(expectedResponse);

        // When (Act)
        PatientResponseDTO  actualResponse = patientService.updatePatient(patientId, request);

        // Then (Assert)
        assertNotNull(actualResponse);
        assertEquals(expectedResponse.id(), actualResponse.id());
        assertEquals(expectedResponse.name(), actualResponse.name());

        verify(patientRepository, times(1)).findById(patientId);
        verify(patientRepository, times(1)).save(patient);
        verify(patientMapper, times(1)).toResponseDTO(patient);
    }

    @Test
    void updatePatient_whenPatientDoesNotExists_throwsResourceNotFoundException() {
        // Given (Arrange)
        Long patientId = 1L;

        PatientRequestDTO request = new PatientRequestDTO(
                "Carlos",
                "Gomez",
                "394485839",
                "cgomez@gmail.com",
                "542983478634"
        );

        when(patientRepository.findById(patientId)).thenReturn(Optional.empty());

        // When & Then (Act & Assert)
        assertThrows(ResourceNotFoundException.class, () -> {
            patientService.updatePatient(patientId, request);
        });

        verify(patientRepository, times(1)).findById(patientId);
        verify(patientRepository, never()).save(any(Patient.class));
    }

    @Test
    void deletePatient_whenPatientExists_deletesPatient() {
        // Given (Arrange)
        Long patientId = 1L;

        Patient patient = new Patient(
                patientId,
                "Carlos",
                "Gomez",
                "394485839",
                "cgomez@gmail.com",
                "542983478634"
        );

        when(patientRepository.findById(patientId)).thenReturn(Optional.of(patient));

        // When (Act)
        patientService.deletePatient(patientId);

        // Then (Assert)
        verify(patientRepository, times(1)).findById(patientId);
        verify(patientRepository, times(1)).delete(patient);
    }

    @Test
    void deletePatient_whenPatientDoesNotExists_throwsResourceNotFoundException() {
        // Given (Arrange)
        Long patientId = 1L;

        when(patientRepository.findById(patientId)).thenReturn(Optional.empty());

        // When & Then (Act & Assert)
        assertThrows(ResourceNotFoundException.class, () -> {
            patientService.deletePatient(patientId);
        });

        verify(patientRepository, times(1)).findById(patientId);
        verify(patientRepository, never()).delete(any(Patient.class));
    }
}
