package com.manuelfoulkes.turnos_medicos.dtos.requests;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class MedicoRequestDTO {

    @NotBlank
    private String nombre;

    private String apellido;

    private String matricula;

    private Long especialidadId;
}
