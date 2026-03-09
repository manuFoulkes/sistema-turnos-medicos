package com.manuelfoulkes.turnos_medicos.mappers;

import com.manuelfoulkes.turnos_medicos.dtos.requests.AppointmentRequestDTO;
import com.manuelfoulkes.turnos_medicos.dtos.responses.AppointmentResponseDTO;
import com.manuelfoulkes.turnos_medicos.entities.Appointment;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {PatientMapper.class, DoctorMapper.class})
public interface AppointmentMapper {
    AppointmentResponseDTO toResponseDTO(Appointment appointment);
}
