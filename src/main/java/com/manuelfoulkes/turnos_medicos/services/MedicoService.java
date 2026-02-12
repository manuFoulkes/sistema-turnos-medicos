package com.manuelfoulkes.turnos_medicos.services;

import com.manuelfoulkes.turnos_medicos.entities.Medico;
import com.manuelfoulkes.turnos_medicos.repositories.MedicoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MedicoService {

    private final MedicoRepository medicoRepository;

    public Medico crearMedico(Medico medico) {
        return medicoRepository.save(medico);
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
