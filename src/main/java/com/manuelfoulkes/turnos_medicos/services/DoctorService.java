package com.manuelfoulkes.turnos_medicos.services;

import com.manuelfoulkes.turnos_medicos.dtos.requests.DoctorRequestDTO;
import com.manuelfoulkes.turnos_medicos.dtos.responses.DoctorResponseDTO;
import com.manuelfoulkes.turnos_medicos.entities.Doctor;
import com.manuelfoulkes.turnos_medicos.entities.Specialty;
import com.manuelfoulkes.turnos_medicos.exceptions.custom.ResourceAlreadyExistsException;
import com.manuelfoulkes.turnos_medicos.exceptions.custom.ResourceNotFoundException;
import com.manuelfoulkes.turnos_medicos.mappers.DoctorMapper;
import com.manuelfoulkes.turnos_medicos.repositories.SpecialtyRepository;
import com.manuelfoulkes.turnos_medicos.repositories.DoctorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DoctorService {

    private final DoctorRepository doctorRepository;
    private final SpecialtyRepository specialtyRepository;
    private final DoctorMapper doctorMapper;

    public DoctorResponseDTO getDoctorById(Long id) {
        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Médico no encontrado"));

        return doctorMapper.toResponseDTO(doctor);
    }

    public List<DoctorResponseDTO> getAllDoctors() {
        List<Doctor> doctors = doctorRepository.findAll();

        List<DoctorResponseDTO> doctorsResponse = new ArrayList<>();

        for (Doctor doctor : doctors) {
            doctorsResponse.add(doctorMapper.toResponseDTO(doctor));
        }

        return doctorsResponse;
    }

    public DoctorResponseDTO createDoctor(DoctorRequestDTO doctorRequestDTO) {
        String licenseNumber = doctorRequestDTO.licenseNumber();

        if (doctorRepository.findByLicenseNumber(licenseNumber).isPresent()) {
            throw new ResourceAlreadyExistsException("El medico ya existe");
        }

        Long specialtyId = doctorRequestDTO.specialtyId();

        Specialty specialty = specialtyRepository.findById(specialtyId)
                .orElseThrow(() -> new ResourceNotFoundException("La especialidad no existe"));

        Doctor newDoctor = doctorMapper.toEntity(doctorRequestDTO);
        newDoctor.setSpecialty(specialty);

        Doctor doctor = doctorRepository.save(newDoctor);

        return doctorMapper.toResponseDTO(newDoctor);
    }

    // Revisar donde va la implementación de mappers para request -> entity en todos los services
    public DoctorResponseDTO updateDoctor(Long id, DoctorRequestDTO doctorRequest) {
         Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Medico no encontrado"));

        Long specialtyId = doctorRequest.specialtyId();

        Specialty specialty = specialtyRepository.findById(specialtyId)
                        .orElseThrow(() -> new ResourceNotFoundException("Especialidad no encontrada"));

        doctor.setName(doctorRequest.name());
        doctor.setLastName(doctorRequest.lastName());
        doctor.setLicenseNumber(doctorRequest.licenseNumber());
        doctor.setSpecialty(specialty);

        Doctor updated = doctorRepository.save(doctor);

        return doctorMapper.toResponseDTO(updated);
    }

    public void deleteDoctor(Long id) {
        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Medico no encontrado"));

        doctorRepository.delete(doctor);
    }
}
