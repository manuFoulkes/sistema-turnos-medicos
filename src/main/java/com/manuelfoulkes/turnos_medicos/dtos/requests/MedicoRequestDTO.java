package com.manuelfoulkes.turnos_medicos.dtos.requests;

import jakarta.validation.constraints.NotBlank;

public record MedicoRequestDTO (

    @NotBlank(message = "El nombre es obligatorio")
    String nombre,

    @NotBlank(message = "El apellido es obligatorio")
    String apellido,

    @NotBlank(message = "La matrícula es obligatoria")
    String matricula,

    @NotBlank(message = "La especialidad es obligatoria")
    Long especialidadId
) {}
