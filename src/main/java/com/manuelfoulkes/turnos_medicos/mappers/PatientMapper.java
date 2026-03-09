package com.manuelfoulkes.turnos_medicos.mappers;

import com.manuelfoulkes.turnos_medicos.dtos.requests.PatientRequestDTO;
import com.manuelfoulkes.turnos_medicos.dtos.responses.PatientResponseDTO;
import com.manuelfoulkes.turnos_medicos.entities.Patient;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PatientMapper {
    PatientResponseDTO toResponseDTO(Patient entity);
    Patient toEntity(PatientRequestDTO request);
}
