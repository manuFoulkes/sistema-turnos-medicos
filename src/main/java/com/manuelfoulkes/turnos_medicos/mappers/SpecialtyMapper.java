package com.manuelfoulkes.turnos_medicos.mappers;

import com.manuelfoulkes.turnos_medicos.dtos.requests.SpecialtyRequestDTO;
import com.manuelfoulkes.turnos_medicos.dtos.responses.SpecialtyResponseDTO;
import com.manuelfoulkes.turnos_medicos.entities.Specialty;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SpecialtyMapper {
    SpecialtyResponseDTO toResponseDTO(Specialty specialty);
    Specialty toEntity(SpecialtyRequestDTO  request);
}
