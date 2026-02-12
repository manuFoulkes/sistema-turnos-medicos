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

    public Medico getMedicoById(Long id) {
        return medicoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Médico no encontrado"));
    }

    public List<Medico> getAllMedicos() {
        return medicoRepository.findAll();
    }

    public Medico updateMedico(Long id, Medico medico) {
        return medico;
    }

    public void deleteMedico(Long id) {
        Medico medico = medicoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Medico con ID " + id + " no encontrado"));

        medicoRepository.delete(medico);
    }
}
