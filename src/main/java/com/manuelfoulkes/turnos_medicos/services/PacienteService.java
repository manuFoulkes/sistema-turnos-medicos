package com.manuelfoulkes.turnos_medicos.services;

import com.manuelfoulkes.turnos_medicos.dtos.requests.PacienteRequestDTO;
import com.manuelfoulkes.turnos_medicos.dtos.responses.PacienteResponseDTO;
import com.manuelfoulkes.turnos_medicos.entities.Paciente;
import com.manuelfoulkes.turnos_medicos.repositories.PacienteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PacienteService {

    private final PacienteRepository pacienteRepository;

    public PacienteResponseDTO crearPaciente(PacienteRequestDTO pacienteRequest) {
        String dniPaciente = pacienteRequest.dni();

        if (pacienteRepository.findByDni(dniPaciente).isPresent()) {
            throw new RuntimeException("El paciente ya está registrado");
        }

        Paciente nuevoPaciente = new Paciente();

        nuevoPaciente.setNombre(pacienteRequest.nombre());
        nuevoPaciente.setApellido(pacienteRequest.apellido());
        nuevoPaciente.setDni(pacienteRequest.dni());
        nuevoPaciente.setEmail(pacienteRequest.email());
        nuevoPaciente.setTelefono(pacienteRequest.telefono());

        nuevoPaciente = pacienteRepository.save(nuevoPaciente);

        return new PacienteResponseDTO(
                nuevoPaciente.getId(),
                nuevoPaciente.getNombre(),
                nuevoPaciente.getApellido(),
                nuevoPaciente.getDni(),
                nuevoPaciente.getEmail(),
                nuevoPaciente.getTelefono()
        );
    }

    public PacienteResponseDTO getById(Long id) {
        Paciente paciente = pacienteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Paciente no encontrado"));

        return new PacienteResponseDTO(
                paciente.getId(),
                paciente.getNombre(),
                paciente.getApellido(),
                paciente.getDni(),
                paciente.getEmail(),
                paciente.getTelefono()
        );
    }

    public List<PacienteResponseDTO> getAllPacientes() {
        List<Paciente> pacientes = pacienteRepository.findAll();

        List<PacienteResponseDTO> pacientesResponse = new ArrayList<>();

        for (Paciente p : pacientes) {
            PacienteResponseDTO pacienteResponse = new PacienteResponseDTO(
                    p.getId(),
                    p.getNombre(),
                    p.getApellido(),
                    p.getDni(),
                    p.getEmail(),
                    p.getTelefono()
            );

            pacientesResponse.add(pacienteResponse)
        }

        return pacientesResponse;
    }

    public PacienteResponseDTO updatePaciente(Long id, PacienteRequestDTO pacienteRequest) {
        Paciente paciente = pacienteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Paciente no encontrado"));

        paciente.setNombre(pacienteRequest.nombre());
        paciente.setApellido(pacienteRequest.apellido());
        paciente.setDni(pacienteRequest.dni());
        paciente.setEmail(pacienteRequest.email());
        paciente.setTelefono(pacienteRequest.telefono());

        paciente = pacienteRepository.save(paciente);

        return new PacienteResponseDTO(
                paciente.getId(),
                paciente.getNombre(),
                paciente.getApellido(),
                paciente.getDni(),
                paciente.getEmail(),
                paciente.getTelefono()
        );
    }

    public void deletePaciente(Long id) {
        Paciente paciente = pacienteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Paciente no encontrado"));

        pacienteRepository.delete(paciente);
    }
}
