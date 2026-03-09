package com.manuelfoulkes.turnos_medicos.mappers;

import com.manuelfoulkes.turnos_medicos.dtos.requests.DoctorRequestDTO;
import com.manuelfoulkes.turnos_medicos.dtos.responses.DoctorResponseDTO;
import com.manuelfoulkes.turnos_medicos.entities.Doctor;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {SpecialtyMapper.class})
public interface DoctorMapper {
    DoctorResponseDTO toResponseDTO(Doctor doctor);

    @Mapping(target = "specialty", ignore = true)
    Doctor toEntity(DoctorRequestDTO request);
}
