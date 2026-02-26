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

    // TODO: Implementar mappers y limpiar
    public TurnoResponseDTO cancelarTurno(Long pacienteId, Long turnoId) {
        Paciente paciente = pacienteRepository.findById(pacienteId)
                .orElseThrow(() -> new RuntimeException("Paciente no encontrado"));

        Turno turno = turnoRepository.findById(turnoId)
                .orElseThrow(() -> new RuntimeException("Turno no encontrado"));

        if(!turno.getPaciente().getId().equals(pacienteId)) {
            throw new RuntimeException("No existe un turno asignado con ese ID");
        }

        if(turno.getEstado().equals(EstadoTurno.CANCELADO)) {
            throw new RuntimeException("El turno ya fue cancelado");
        }

        if(turno.getEstado().equals(EstadoTurno.COMPLETADO)) {
            throw new RuntimeException("El turno ya ha sido completado");
        }

        LocalDateTime ahora = LocalDateTime.now();
        LocalDateTime limite = turno.getFechaHora().minusHours(48);

        if(ahora.isAfter(limite)) {
            throw new RuntimeException("No se puede cancelar un turno con menos de 48 hs de anticipación");
        }

        turno.setEstado(EstadoTurno.CANCELADO);

        Turno turnoCancelado = turnoRepository.save(turno);

        PacienteResponseDTO pacienteResponse = new PacienteResponseDTO(
                paciente.getId(),
                paciente.getNombre(),
                paciente.getApellido(),
                paciente.getDni(),
                paciente.getEmail(),
                paciente.getTelefono()
        );

        Medico medico = turnoCancelado.getMedico();

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
                turnoCancelado.getId(),
                turnoCancelado.getFechaHora(),
                turnoCancelado.getEstado(),
                pacienteResponse,
                medicoResponse,
                turnoCancelado.getFechaCreacion()
        );
    }

    // TODO: Implementar mapppers y limpiar
    public TurnoResponseDTO modificarTurno(Long pacienteId, Long turnoId, TurnoRequestDTO turnoRequest) {
        Paciente paciente = pacienteRepository.findById(pacienteId)
                .orElseThrow(() -> new RuntimeException("El paciente no existe"));

        Turno turno = turnoRepository.findById(turnoId)
                .orElseThrow(() -> new RuntimeException("El turno no existe"));

        if(!turno.getPaciente().getId().equals(pacienteId)) {
            throw new RuntimeException("El turno no pertenece al paciente");
        }

        if(turno.getEstado().equals(EstadoTurno.CANCELADO)) {
            throw new RuntimeException("El turno ya ha sido cancelado");
        }

        if(turno.getEstado().equals(EstadoTurno.COMPLETADO)) {
            throw new RuntimeException("El turno ya ha sido completado");
        }

        LocalDateTime ahora = LocalDateTime.now();
        LocalDateTime limite = turno.getFechaHora().minusHours(48);

        if(ahora.isAfter(limite)) {
            throw new RuntimeException("No se puede modificar un turno con menos de 48 hs de anticipación");
        }

        Medico medico = medicoRepository.findById(turnoRequest.medicoId())
                        .orElseThrow(() -> new RuntimeException("El médico no existe"));

        boolean existeTurno = existeTurno(medico.getId(), turnoRequest.fechaHora(), EstadoTurno.CANCELADO, turnoId);

        if(existeTurno) {
            throw new RuntimeException("El médico ya tiene un turno asignado en ese horario");
        }

        if(ahora.isAfter(turnoRequest.fechaHora())) {
            throw new RuntimeException("La fecha debe ser futura");
        }

        turno.setFechaHora(turnoRequest.fechaHora());
        turno.setMedico(medico);

        Turno turnoModificado = turnoRepository.save(turno);
        Especialidad especialidad = medico.getEspecialidad();

        PacienteResponseDTO pacienteResponse = new PacienteResponseDTO(
                paciente.getId(),
                paciente.getNombre(),
                paciente.getApellido(),
                paciente.getDni(),
                paciente.getEmail(),
                paciente.getTelefono()
        );

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
                turnoModificado.getId(),
                turnoModificado.getFechaHora(),
                turnoModificado.getEstado(),
                pacienteResponse,
                medicoResponse,
                turnoModificado.getFechaCreacion()
        );
    }

    // TODO: Completar
    public void completarTurno(Long pacienteId, Long turnoId) {}

    private boolean existeTurno(Long medicoId, LocalDateTime fechaHora, EstadoTurno estadoTurno) {
        return turnoRepository.existsByMedicoIdAndFechaHoraAndEstadoNot(medicoId, fechaHora, estadoTurno);
    }

    private boolean existeTurno(Long medicoId, LocalDateTime fechaHora, EstadoTurno estadoTurno, Long turnoId) {
        return turnoRepository.existsByMedicoIdAndFechaHoraAndEstadoNotAndIdNot(medicoId, fechaHora, estadoTurno, turnoId);
    }

    private int getTurnosActivos(Long pacienteId, EstadoTurno estadoTurno, LocalDateTime fechaHora) {
        return turnoRepository.countByPacienteIdAndEstadoAndFechaHoraAfter(pacienteId, estadoTurno, fechaHora);
    }
}
