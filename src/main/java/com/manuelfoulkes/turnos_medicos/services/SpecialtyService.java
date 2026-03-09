package com.manuelfoulkes.turnos_medicos.services;

import com.manuelfoulkes.turnos_medicos.dtos.requests.SpecialtyRequestDTO;
import com.manuelfoulkes.turnos_medicos.dtos.responses.SpecialtyResponseDTO;
import com.manuelfoulkes.turnos_medicos.entities.Specialty;
import com.manuelfoulkes.turnos_medicos.exceptions.custom.ResourceAlreadyExistsException;
import com.manuelfoulkes.turnos_medicos.exceptions.custom.ResourceNotFoundException;
import com.manuelfoulkes.turnos_medicos.mappers.SpecialtyMapper;
import com.manuelfoulkes.turnos_medicos.repositories.SpecialtyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SpecialtyService {

    private final SpecialtyRepository specialtyRepository;
    private final SpecialtyMapper specialtyMapper;

    // TODO: Refactor: Usar DTOs en todos los métodos

    public SpecialtyResponseDTO getSpecialtyById(Long id) {
        Specialty specialty = specialtyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Especialidad no encontrada"));

        return specialtyMapper.toResponseDTO(specialty);
    }

    public List<SpecialtyResponseDTO> getAllSpecialties() {
        List<Specialty> specialties = specialtyRepository.findAll();
        List<SpecialtyResponseDTO> specialtiesResponse = new ArrayList<>();

        for(Specialty specialty : specialties) {
            specialtiesResponse.add(specialtyMapper.toResponseDTO(specialty));
        }

        return specialtiesResponse;
    }

    public SpecialtyResponseDTO createSpecialty(SpecialtyRequestDTO specialtyRequest) {
        if(specialtyRepository.findByName(specialtyRequest.name()).isPresent()) {
            throw new ResourceAlreadyExistsException("La especialidad ya existe");
        }

        Specialty specialty = specialtyMapper.toEntity(specialtyRequest);
        Specialty newSpecialty = specialtyRepository.save(specialty);

        return specialtyMapper.toResponseDTO(newSpecialty);
    }

    public SpecialtyResponseDTO updateSpecialty(Long id, SpecialtyRequestDTO specialtyRequest) {
        Specialty specialty = specialtyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Especialidad no encontrada"));

        specialty.setName(specialtyRequest.name());

        Specialty updatedSpecialty = specialtyRepository.save(specialty);

        return new SpecialtyResponseDTO(updatedSpecialty.getId(), updatedSpecialty.getName());
    }

    public void deleteSpecialty(Long id) {
        specialtyRepository.deleteById(id);
    }
}
