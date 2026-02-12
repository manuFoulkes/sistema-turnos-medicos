package com.manuelfoulkes.turnos_medicos.services;

import com.manuelfoulkes.turnos_medicos.repositories.PacienteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PacienteService {

    private final PacienteRepository pacienteRepository;

    public PacienteResponseDTO crearPaciente(PacienteRequestDTO pacienteRequest) {
        // TODO: Completar
        return null;
    }

    public PacienteResponseDTO getById(Long id) {
        // TODO: Completar
        return null;
    }

    public List<PacienteResponseDTO> getAllPacientes() {
        // TODO: Completar
        return null;
    }

    public PacienteResponseDTO updatePaciente(Long id, PacienteRequestDTO pacienteRequest) {
        // TODO: Completar
        return null;
    }

    public void deletePaciente(Long id) {
        // TODO: Completar
    }
}
