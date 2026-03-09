package com.manuelfoulkes.turnos_medicos.services;

import com.manuelfoulkes.turnos_medicos.dtos.requests.PatientRequestDTO;
import com.manuelfoulkes.turnos_medicos.dtos.responses.PatientResponseDTO;
import com.manuelfoulkes.turnos_medicos.entities.Patient;
import com.manuelfoulkes.turnos_medicos.exceptions.custom.ResourceAlreadyExistsException;
import com.manuelfoulkes.turnos_medicos.exceptions.custom.ResourceNotFoundException;
import com.manuelfoulkes.turnos_medicos.mappers.PatientMapper;
import com.manuelfoulkes.turnos_medicos.repositories.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PatientService {

    private final PatientRepository patientRepository;
    private final PatientMapper patientMapper;

    public PatientResponseDTO getById(Long id) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Paciente no encontrado"));

        return patientMapper.toResponseDTO(patient);
    }

    public List<PatientResponseDTO> getAllPatients() {
        List<Patient> patients = patientRepository.findAll();

        List<PatientResponseDTO> pacientesResponse = new ArrayList<>();

        for (Patient patient : patients) {
            pacientesResponse.add(patientMapper.toResponseDTO(patient));
        }

        return pacientesResponse;
    }

    public PatientResponseDTO createPatient(PatientRequestDTO patientRequest) {
        String nationalId = patientRequest.nationalId();

        if(patientRepository.findByNationalId(nationalId).isPresent()) {
            throw new ResourceAlreadyExistsException("El paciente ya está registrado");
        }

        Patient patient = patientMapper.toEntity(patientRequest);

        Patient newPatient = patientRepository.save(patient);

        return patientMapper.toResponseDTO(newPatient);
    }

    public PatientResponseDTO updatePatient(Long id, PatientRequestDTO patientRequest) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Paciente no encontrado"));

        patient.setName(patientRequest.name());
        patient.setLastName(patientRequest.lastName());
        patient.setNationalId(patientRequest.nationalId());
        patient.setEmail(patientRequest.email());
        patient.setPhoneNumber(patientRequest.phoneNumber());

        Patient newPatient = patientRepository.save(patient);

        return patientMapper.toResponseDTO(newPatient);
    }

    public void deletePatient(Long id) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Paciente no encontrado"));

        patientRepository.delete(patient);
    }
}
