package com.manuelfoulkes.turnos_medicos.services;

import com.manuelfoulkes.turnos_medicos.dtos.requests.TurnoRequestDTO;
import com.manuelfoulkes.turnos_medicos.dtos.responses.EspecialidadResponseDTO;
import com.manuelfoulkes.turnos_medicos.dtos.responses.MedicoResponseDTO;
import com.manuelfoulkes.turnos_medicos.dtos.responses.PacienteResponseDTO;
import com.manuelfoulkes.turnos_medicos.dtos.responses.TurnoResponseDTO;
import com.manuelfoulkes.turnos_medicos.entities.*;
import com.manuelfoulkes.turnos_medicos.repositories.MedicoRepository;
import com.manuelfoulkes.turnos_medicos.repositories.PacienteRepository;
import com.manuelfoulkes.turnos_medicos.repositories.TurnoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class TurnoService {

    private final TurnoRepository turnoRepository;
    private final PacienteRepository pacienteRepository;
    private final MedicoRepository medicoRepository;

    // TODO: Revisar y limpiar. Sacar funcionalidades a métodos privados
    public TurnoResponseDTO registrarTurno(TurnoRequestDTO turnoRequest) {
        // TurnoRequestDTO viene con: fechaHora, pacienteId, medicoId
        Long pacienteId = turnoRequest.pacienteId();
        Long medicoId = turnoRequest.medicoId();

        // 1 - Validar que el paciente existe
        Paciente paciente = pacienteRepository.findById(pacienteId)
                .orElseThrow(() -> new RuntimeException("Paciente no encontrado"));

        // 2 - Validar que el medico existe
        Medico medico = medicoRepository.findById(medicoId)
                .orElseThrow(() ->  new RuntimeException("Medico no encontrado"));

        // 3 - Validar que el horario sea posterior a ahora
        if(turnoRequest.fechaHora().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Fecha inválida");
        }

        // TODO: Crear existeTurno()
        // 4 - Validar que no exista ya un turno asignado en ese horario con ese medico
        if(!existeTurno(medicoId, turnoRequest.fechaHora())) {
            throw new RuntimeException("El medico ya tiene un turno asignado en ese horario");
        }

        // TODO: Crear getTurnosActivos()
        // 5 - Validar límite de turnos activos del paciente
        int turnosActivos = getTurnosActivos(pacienteId, EstadoTurno.CANCELADO, LocalDateTime.now());

        if (turnosActivos > 3) {
            throw new RuntimeException("Cantidad de turnos por paciente excedida");
        }

        // 6 - Crear el turno
        Turno nuevoTurno =  new Turno();
        nuevoTurno.setFechaHora(turnoRequest.fechaHora());
        nuevoTurno.setPaciente(paciente);
        nuevoTurno.setMedico(medico);

        nuevoTurno = turnoRepository.save(nuevoTurno);

        // 7 - Crear PacienteResponseDTO
        PacienteResponseDTO pacienteResponse = new PacienteResponseDTO(
                paciente.getId(),
                paciente.getNombre(),
                paciente.getApellido(),
                paciente.getDni(),
                paciente.getEmail(),
                paciente.getTelefono()
        );

        // 8 - Crear EspecialidadResponseDTO y MedicoResponseDTO
        Especialidad especialidad = medico.getEspecialidad();

        EspecialidadResponseDTO especialidadResponse = new EspecialidadResponseDTO(
                especialidad.getId(),
                especialidad.getNombre()
        );

        MedicoResponseDTO medicoResponse =  new MedicoResponseDTO(
                medico.getId(),
                medico.getNombre(),
                medico.getApellido(),
                medico.getMatricula(),
                especialidadResponse
        );

        // 9 - Mapear a TurnoResponseDTO y retornarlo
        return new TurnoResponseDTO(
                nuevoTurno.getId(),
                nuevoTurno.getFechaHora(),
                nuevoTurno.getEstado(),
                pacienteResponse,
                medicoResponse,
                nuevoTurno.getFechaCreacion()
        );
    }
}
