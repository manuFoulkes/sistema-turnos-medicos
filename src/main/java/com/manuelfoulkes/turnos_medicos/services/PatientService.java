package com.manuelfoulkes.turnos_medicos.services;

import com.manuelfoulkes.turnos_medicos.dtos.requests.PatientRequestDTO;
import com.manuelfoulkes.turnos_medicos.dtos.responses.PatientResponseDTO;
import com.manuelfoulkes.turnos_medicos.entities.Patient;
import com.manuelfoulkes.turnos_medicos.exceptions.custom.ResourceAlreadyExistsException;
import com.manuelfoulkes.turnos_medicos.exceptions.custom.ResourceNotFoundException;
import com.manuelfoulkes.turnos_medicos.repositories.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PatientService {

    private final PatientRepository patientRepository;

    public PatientResponseDTO getById(Long id) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Paciente no encontrado"));

        return new PatientResponseDTO(
                patient.getId(),
                patient.getName(),
                patient.getLastName(),
                patient.getNationalId(),
                patient.getEmail(),
                patient.getPhoneNumber()
        );
    }

    public List<PatientResponseDTO> getAllPatients() {
        List<Patient> patients = patientRepository.findAll();

        List<PatientResponseDTO> pacientesResponse = new ArrayList<>();

        for (Patient p : patients) {
            PatientResponseDTO pacienteResponse = new PatientResponseDTO(
                    p.getId(),
                    p.getName(),
                    p.getLastName(),
                    p.getNationalId(),
                    p.getEmail(),
                    p.getPhoneNumber()
            );

            pacientesResponse.add(pacienteResponse);
        }

        return pacientesResponse;
    }

    public PatientResponseDTO createPatient(PatientRequestDTO patientRequest) {
        String nationalId = patientRequest.nationalId();

        if(patientRepository.findByNationalId(nationalId).isPresent()) {
            throw new ResourceAlreadyExistsException("El paciente ya está registrado");
        }

        Patient newPatient = new Patient();

        newPatient.setName(patientRequest.name());
        newPatient.setLastName(patientRequest.lastName());
        newPatient.setNationalId(patientRequest.nationalId());
        newPatient.setEmail(patientRequest.email());
        newPatient.setPhoneNumber(patientRequest.phoneNumber());

        newPatient = patientRepository.save(newPatient);

        return new PatientResponseDTO(
                newPatient.getId(),
                newPatient.getName(),
                newPatient.getLastName(),
                newPatient.getNationalId(),
                newPatient.getEmail(),
                newPatient.getPhoneNumber()
        );
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

        return new PatientResponseDTO(
                newPatient.getId(),
                newPatient.getName(),
                newPatient.getLastName(),
                newPatient.getNationalId(),
                newPatient.getEmail(),
                newPatient.getPhoneNumber()
        );
    }

    public void deletePatient(Long id) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Paciente no encontrado"));

        patientRepository.delete(patient);
    }
}
