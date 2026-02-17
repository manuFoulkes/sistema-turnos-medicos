package com.manuelfoulkes.turnos_medicos.services;

import com.manuelfoulkes.turnos_medicos.dtos.requests.MedicoRequestDTO;
import com.manuelfoulkes.turnos_medicos.dtos.responses.EspecialidadResponseDTO;
import com.manuelfoulkes.turnos_medicos.dtos.responses.MedicoResponseDTO;
import com.manuelfoulkes.turnos_medicos.entities.Especialidad;
import com.manuelfoulkes.turnos_medicos.entities.Medico;
import com.manuelfoulkes.turnos_medicos.repositories.EspecialidadRepository;
import com.manuelfoulkes.turnos_medicos.repositories.MedicoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
// TODO: Implementar mappers. Revisar validaciones
@Service
@RequiredArgsConstructor
public class MedicoService {

    private final MedicoRepository medicoRepository;
    private final EspecialidadRepository especialidadRepository;

    public MedicoResponseDTO crearMedico(MedicoRequestDTO medicoRequestDTO) {
        String matricula = medicoRequestDTO.matricula();

        if (medicoRepository.findByMatricula(matricula).isPresent()) {
            throw new RuntimeException("El medico ya existe");
        }

        Long especialidadId = medicoRequestDTO.especialidadId();

        Especialidad especialidad = especialidadRepository.findById(especialidadId)
                .orElseThrow(() -> new RuntimeException("La especialidad con Id " + especialidadId + " no existe"));

        EspecialidadResponseDTO especialidadResponseDTO = new EspecialidadResponseDTO(
                especialidad.getId(),
                especialidad.getNombre()
        );

        Medico nuevoMedico = new Medico();

        nuevoMedico.setNombre(medicoRequestDTO.nombre());
        nuevoMedico.setApellido(medicoRequestDTO.apellido());
        nuevoMedico.setMatricula(medicoRequestDTO.matricula());
        nuevoMedico.setEspecialidad(especialidad);

        nuevoMedico = medicoRepository.save(nuevoMedico);

        return new MedicoResponseDTO(
                nuevoMedico.getId(),
                nuevoMedico.getNombre(),
                nuevoMedico.getApellido(),
                nuevoMedico.getMatricula(),
                especialidadResponseDTO
        );
    }
    
    public MedicoResponseDTO getMedicoById(Long id) {
        Medico medico = medicoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Médico no encontrado"));

        Especialidad especialidad = medico.getEspecialidad();

        EspecialidadResponseDTO especialidadResponseDTO = new EspecialidadResponseDTO(
                especialidad.getId(),
                especialidad.getNombre()
        );

        return new MedicoResponseDTO(
                medico.getId(),
                medico.getNombre(),
                medico.getApellido(),
                medico.getMatricula(),
                especialidadResponseDTO
        );
    }

    // TODO: Implementar DTO
    public List<Medico> getAllMedicos() {
        return medicoRepository.findAll();
    }

    public MedicoResponseDTO updateMedico(Long id, MedicoRequestDTO medicoRequest) {
        Medico medico = medicoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Medico no encontrado"));

        Long especialidadId = medicoRequest.especialidadId();

        Especialidad especialidad = especialidadRepository.findById(especialidadId)
                        .orElseThrow(() -> new RuntimeException("Especialidad no válida"));

        medico.setNombre(medicoRequest.nombre());
        medico.setApellido(medicoRequest.apellido());
        medico.setMatricula(medicoRequest.matricula());
        medico.setEspecialidad(especialidad);

        medico = medicoRepository.save(medico);

        EspecialidadResponseDTO especialidadResponse = new EspecialidadResponseDTO(
                especialidad.getId(),
                especialidad.getNombre()
        );

        return new MedicoResponseDTO(
                medico.getId(),
                medico.getNombre(),
                medico.getApellido(),
                medico.getMatricula(),
                especialidadResponse
        );
    }

    public void deleteMedico(Long id) {
        Medico medico = medicoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Medico con ID " + id + " no encontrado"));

        medicoRepository.delete(medico);
    }
}
