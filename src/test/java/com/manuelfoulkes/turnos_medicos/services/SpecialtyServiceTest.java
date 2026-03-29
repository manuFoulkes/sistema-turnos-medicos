package com.manuelfoulkes.turnos_medicos.services;

import com.manuelfoulkes.turnos_medicos.dtos.requests.SpecialtyRequestDTO;
import com.manuelfoulkes.turnos_medicos.dtos.responses.SpecialtyResponseDTO;
import com.manuelfoulkes.turnos_medicos.entities.Specialty;
import com.manuelfoulkes.turnos_medicos.exceptions.custom.ResourceAlreadyExistsException;
import com.manuelfoulkes.turnos_medicos.exceptions.custom.ResourceNotFoundException;
import com.manuelfoulkes.turnos_medicos.mappers.SpecialtyMapper;
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
public class SpecialtyServiceTest {

    @Mock
    private SpecialtyRepository specialtyRepository;

    @Mock
    private SpecialtyMapper specialtyMapper;

    @InjectMocks
    private SpecialtyService specialtyService;

    @Test
    void getSpecialtyById_whenSpecialtyExist_returnsSpecialtyResponseDTO() {
        // Given (Arrange)
        Long specialtyId = 1L;
        String name = "Cardiology";

        Specialty specialty = new Specialty(specialtyId, name);

        SpecialtyResponseDTO expectedResponse = new SpecialtyResponseDTO(specialtyId, name);

        when(specialtyRepository.findById(specialtyId)).thenReturn(Optional.of(specialty));
        when(specialtyMapper.toResponseDTO(specialty)).thenReturn(expectedResponse);

        // When (Act)
        SpecialtyResponseDTO actualResponse = specialtyService.getSpecialtyById(specialtyId);

        // Then (Assert)
        assertNotNull(actualResponse);
        assertEquals(expectedResponse.name(), actualResponse.name());

        verify(specialtyRepository, times(1)).findById(specialtyId);
        verify(specialtyMapper).toResponseDTO(specialty);
    }

    @Test
    void getSpecialtyById_whenSpecialtyDoesNotExist_throwsResourceNotFoundException() {
        // Given (Arrange)
        Long  specialtyId = 1L;

        when(specialtyRepository.findById(specialtyId)).thenReturn(Optional.empty());

        // When & Then (Act & Assert)
        assertThrows(ResourceNotFoundException.class, () -> {
            specialtyService.getSpecialtyById(specialtyId);
        });

        verify(specialtyRepository, times(1)).findById(specialtyId);
    }

    @Test
    void getAllSpecialties_whenSpecialtiesExist_returnsSpecialtyResponseDTOList() {
        // Given (Arrange)
        Specialty specialty1 = new Specialty(1L, "Cardiology");
        Specialty specialty2 = new Specialty(2L, "Pediatrics");

        List<Specialty> specialties = List.of(specialty1, specialty2);

        SpecialtyResponseDTO dto1 = new SpecialtyResponseDTO(1L, "Cardiology");
        SpecialtyResponseDTO dto2 = new SpecialtyResponseDTO(2L, "Pediatrics");

        when(specialtyRepository.findAll()).thenReturn(specialties);
        when(specialtyMapper.toResponseDTO(specialty1)).thenReturn(dto1);
        when(specialtyMapper.toResponseDTO(specialty2)).thenReturn(dto2);

        // When (Act)
        List<SpecialtyResponseDTO> result = specialtyService.getAllSpecialties();

        // Then (Assert)
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Cardiology", result.get(0).name());
        assertEquals("Pediatrics", result.get(1).name());

        verify(specialtyRepository, times(1)).findAll();
        verify(specialtyMapper, times(2)).toResponseDTO(any(Specialty.class));
    }

    @Test
    void getAllSpecialties_whenNoSpecialtiesExist_returnsEmptyList() {
        // Given (Arrange)
        when(specialtyRepository.findAll()).thenReturn(Collections.emptyList());

        // When (Act)
        List<SpecialtyResponseDTO> result = specialtyService.getAllSpecialties();

        // Then (Assert)
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(specialtyRepository, times(1)).findAll();
    }

    @Test
    void createSpecialty_whenRequestIsValid_returnsSpecialResponseDTO() {
        // Given (Arrange)
        Specialty specialty = new Specialty(1L, "Cardiology");

        SpecialtyRequestDTO request = new SpecialtyRequestDTO("Cardiology");

        SpecialtyResponseDTO expectedResponse = new SpecialtyResponseDTO(1L, "Cardiology");

        when(specialtyRepository.findByName(request.name())).thenReturn(Optional.empty());
        when(specialtyMapper.toEntity(request)).thenReturn(specialty);
        when(specialtyRepository.save(specialty)).thenReturn(specialty);
        when(specialtyMapper.toResponseDTO(specialty)).thenReturn(expectedResponse);

        // When (Act)
        SpecialtyResponseDTO actualResponse =  specialtyService.createSpecialty(request);

        // Then (Assert)
        assertEquals(expectedResponse.id(), actualResponse.id());
        assertEquals(expectedResponse.name(), actualResponse.name());

        verify(specialtyRepository, times(1)).findByName(request.name());
        verify(specialtyMapper, times(1)).toEntity(request);
        verify(specialtyRepository, times(1)).save(specialty);
        verify(specialtyMapper, times(1)).toResponseDTO(specialty);
    }

    @Test
    void createSpecialty_whenSpecialtyAlreadyExist_throwsResourceAlreadyExistException() {
        // Given (Arrange)
        String name = "Cardiology";

        Specialty specialty = new Specialty(1L, name);

        SpecialtyRequestDTO request = new SpecialtyRequestDTO(name);

        when(specialtyRepository.findByName(name)).thenReturn(Optional.of(specialty));

        // When (Act)
        assertThrows(ResourceAlreadyExistsException.class, () -> {
            specialtyService.createSpecialty(request);
        });

        // Then (Assert)
        verify(specialtyRepository, times(1)).findByName(name);
        verify(specialtyRepository, never()).save(specialty);
    }

    @Test
    void updateSpecialty_whenRequestIsValid_returnsSpecialtyResponseDTO() {
        // Given (Arrange)
        Long specialtyId = 1L;

        Specialty specialty = new Specialty(specialtyId, "Cardiology");

        SpecialtyRequestDTO request = new SpecialtyRequestDTO("Cardiology");

        SpecialtyResponseDTO expectedResponse = new SpecialtyResponseDTO(specialtyId, "Cardiology");

        when(specialtyRepository.findById(specialtyId)).thenReturn(Optional.of(specialty));
        when(specialtyRepository.save(specialty)).thenReturn(specialty);
        when(specialtyMapper.toResponseDTO(specialty)).thenReturn(expectedResponse);

        // When (Act)
        SpecialtyResponseDTO actualResponse =  specialtyService.updateSpecialty(specialtyId, request);

        // Then (Assert)
        assertNotNull(actualResponse);
        assertEquals(expectedResponse.id(), actualResponse.id());
        assertEquals(expectedResponse.name(), actualResponse.name());

        verify(specialtyRepository, times(1)).findById(specialtyId);
        verify(specialtyRepository, times(1)).save(specialty);
        verify(specialtyMapper, times(1)).toResponseDTO(specialty);
    }

    @Test
    void updateSpecialty_whenSpecialtyDoesNotExist_throwsResourceNotFoundException() {
        // Given (Arrange)
        Long specialtyId = 1L;

        when(specialtyRepository.findById(specialtyId)).thenReturn(Optional.empty());

        // When & Then (Act & Assert)
        assertThrows(ResourceNotFoundException.class, () -> {
            specialtyService.getSpecialtyById(specialtyId);
        });

        verify(specialtyRepository, times(1)).findById(specialtyId);
        verify(specialtyRepository, never()).save(any(Specialty.class));
    }

    @Test
    void deleteSpecialty_whenSpecialtyExists_deleteEspecialty() {
        // Given (Arrange)
        Long specialtyId = 1L;

        Specialty specialty = new Specialty(specialtyId, "Cardiology");

        when(specialtyRepository.findById(specialtyId)).thenReturn(Optional.of(specialty));

        // When (Act)
        specialtyService.deleteSpecialty(specialtyId);

        // Then (Assert)
        verify(specialtyRepository, times(1)).findById(specialtyId);
        verify(specialtyRepository, times(1)).delete(specialty);
    }

    @Test
    void deleteSpecialty_whenSpecialtyDoesNotExist_throwsResourceNotFoundException() {
        // Given (Arrange)
        Long  specialtyId = 1L;

        when(specialtyRepository.findById(specialtyId)).thenReturn(Optional.empty());

        // When & Them (Act & Assert)
        assertThrows(ResourceNotFoundException.class, () -> {
            specialtyService.deleteSpecialty(specialtyId);
        });

        verify(specialtyRepository, times(1)).findById(specialtyId);
    }
}
