package com.manuelfoulkes.turnos_medicos.services;

import com.manuelfoulkes.turnos_medicos.dtos.requests.SpecialtyRequestDTO;
import com.manuelfoulkes.turnos_medicos.dtos.responses.SpecialtyResponseDTO;
import com.manuelfoulkes.turnos_medicos.entities.Specialty;
import com.manuelfoulkes.turnos_medicos.repositories.SpecialtyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SpecialtyService {

    private final SpecialtyRepository specialtyRepository;

    // TODO: Refactor: Usar DTOs en todos los métodos

    public SpecialtyResponseDTO getSpecialtyById(Long id) {
        Specialty specialty = specialtyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Especialidad no encontrada"));

        return new SpecialtyResponseDTO(specialty.getId(), specialty.getName());
    }

    public List<SpecialtyResponseDTO> getAllSpecialties() {
        List<Specialty> specialties = specialtyRepository.findAll();
        List<SpecialtyResponseDTO> specialtiesResponse = new ArrayList<>();

        for(Specialty specialty : specialties) {
            SpecialtyResponseDTO specialtyResponse = new SpecialtyResponseDTO(specialty.getId(), specialty.getName());
            specialtiesResponse.add(specialtyResponse);
        }
        return specialtiesResponse;
    }

    public SpecialtyResponseDTO createSpecialty(SpecialtyRequestDTO specialtyRequest) {
        Specialty specialty = new Specialty();
        specialty.setName(specialtyRequest.name());

        Specialty newSpecialty = specialtyRepository.save(specialty);
        return new SpecialtyResponseDTO(newSpecialty.getId(), newSpecialty.getName());
    }

    public SpecialtyResponseDTO updateSpecialty(Long id, SpecialtyRequestDTO specialtyRequest) {
        Specialty specialty = specialtyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Especialidad no encontrada"));

        specialty.setName(specialtyRequest.name());

        Specialty updatedSpecialty = specialtyRepository.save(specialty);

        return new SpecialtyResponseDTO(updatedSpecialty.getId(), updatedSpecialty.getName());
    }

    public void deleteSpecialty(Long id) {
        specialtyRepository.deleteById(id);
    }
}