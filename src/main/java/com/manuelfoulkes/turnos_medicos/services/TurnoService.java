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

    // TODO: Implementar mappers
    public TurnoResponseDTO registrarTurno(TurnoRequestDTO turnoRequest) {
        Long pacienteId = turnoRequest.pacienteId();
        Long medicoId = turnoRequest.medicoId();
        int maximoTurnos = 3;

        Paciente paciente = pacienteRepository.findById(pacienteId)
                .orElseThrow(() -> new RuntimeException("Paciente no encontrado"));

        Medico medico = medicoRepository.findById(medicoId)
                .orElseThrow(() ->  new RuntimeException("Medico no encontrado"));

        if(turnoRequest.fechaHora().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Fecha inválida");
        }

        if(existeTurno(medicoId, turnoRequest.fechaHora(), EstadoTurno.CANCELADO)) {
            throw new RuntimeException("El medico ya tiene un turno asignado en ese horario");
        }

        int turnosActivos = getTurnosActivos(pacienteId, EstadoTurno.RESERVADO, LocalDateTime.now());

        if (turnosActivos > maximoTurnos) {
            throw new RuntimeException("Cantidad de turnos por paciente excedida");
        }

        Turno nuevoTurno =  new Turno();
        nuevoTurno.setFechaHora(turnoRequest.fechaHora());
        nuevoTurno.setPaciente(paciente);
        nuevoTurno.setMedico(medico);

        nuevoTurno = turnoRepository.save(nuevoTurno);

        PacienteResponseDTO pacienteResponse = new PacienteResponseDTO(
                paciente.getId(),
                paciente.getNombre(),
                paciente.getApellido(),
                paciente.getDni(),
                paciente.getEmail(),
                paciente.getTelefono()
        );

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

        return new TurnoResponseDTO(
                nuevoTurno.getId(),
                nuevoTurno.getFechaHora(),
                nuevoTurno.getEstado(),
                pacienteResponse,
                medicoResponse,
                nuevoTurno.getFechaCreacion()
        );
    }

    //TODO: Falta validación de fecha del turno para cancelar
    // TODO: Implementar mappers
    // TODO: lIMPIAR
    public TurnoResponseDTO cancelarTurno(Long pacienteId, Long turnoId) {
        Paciente paciente = pacienteRepository.findById(pacienteId)
                .orElseThrow(() -> new RuntimeException("Paciente no encontrado"));

        Turno turno = turnoRepository.findById(turnoId)
                .orElseThrow(() -> new RuntimeException("Turno no encontrado"));

        turno.setEstado(EstadoTurno.CANCELADO);

        turno = turnoRepository.save(turno);

        PacienteResponseDTO pacienteResponse = new PacienteResponseDTO(
                paciente.getId(),
                paciente.getNombre(),
                paciente.getApellido(),
                paciente.getDni(),
                paciente.getEmail(),
                paciente.getTelefono()
        );

        Medico medico = turno.getMedico(); // revisar warning

        Especialidad especialidad =  medico.getEspecialidad();

        EspecialidadResponseDTO  especialidadResponse = new EspecialidadResponseDTO(
                especialidad.getId(),
                especialidad.getNombre()
        );

        MedicoResponseDTO medicoResponse = new MedicoResponseDTO(
                medico.getId(),
                medico.getNombre(),
                medico.getApellido(),
                medico.getMatricula(),
                especialidadResponse
        );

        return new TurnoResponseDTO(
                turno.getId(),
                turno.getFechaHora(),
                turno.getEstado(),
                pacienteResponse,
                medicoResponse,
                turno.getFechaCreacion()
        );
    }

    private boolean existeTurno(Long medicoId, LocalDateTime fechaHora, EstadoTurno estadoTurno) {
        return turnoRepository.existsByMedicoIdAndFechaHoraAndEstadoNot(medicoId, fechaHora, estadoTurno);
    }

    private int getTurnosActivos(Long pacienteId, EstadoTurno estadoTurno, LocalDateTime fechaHora) {
        return turnoRepository.countByPacienteIdAndEstadoAndFechaHoraAfter(pacienteId, estadoTurno, fechaHora);
    }
}
