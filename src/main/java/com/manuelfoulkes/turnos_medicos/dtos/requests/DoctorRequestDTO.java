package com.manuelfoulkes.turnos_medicos.dtos.requests;

import jakarta.validation.constraints.NotBlank;

public record DoctorRequestDTO(

    @NotBlank(message = "El nombre es obligatorio")
    String name,

    @NotBlank(message = "El apellido es obligatorio")
    String lastName,

    @NotBlank(message = "La matrícula es obligatoria")
    String licenseNumber,

    @NotBlank(message = "La especialidad es obligatoria")
    Long specialtyId
) {}
