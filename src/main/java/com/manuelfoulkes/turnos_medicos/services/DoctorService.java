package com.manuelfoulkes.turnos_medicos.services;

import com.manuelfoulkes.turnos_medicos.dtos.requests.DoctorRequestDTO;
import com.manuelfoulkes.turnos_medicos.dtos.responses.SpecialtyResponseDTO;
import com.manuelfoulkes.turnos_medicos.dtos.responses.DoctorResponseDTO;
import com.manuelfoulkes.turnos_medicos.entities.Doctor;
import com.manuelfoulkes.turnos_medicos.entities.Specialty;
import com.manuelfoulkes.turnos_medicos.exceptions.custom.ResourceAlreadyExistsException;
import com.manuelfoulkes.turnos_medicos.exceptions.custom.ResourceNotFoundException;
import com.manuelfoulkes.turnos_medicos.repositories.SpecialtyRepository;
import com.manuelfoulkes.turnos_medicos.repositories.DoctorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
// TODO: Implementar mappers. Revisar validaciones
@Service
@RequiredArgsConstructor
public class DoctorService {

    private final DoctorRepository doctorRepository;
    private final SpecialtyRepository specialtyRepository;

    public DoctorResponseDTO getDoctorById(Long id) {
        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Médico no encontrado"));

        Specialty specialty = doctor.getSpecialty();

        SpecialtyResponseDTO specialtyResponseDTO = new SpecialtyResponseDTO(
                specialty.getId(),
                specialty.getName()
        );

        return new DoctorResponseDTO(
                doctor.getId(),
                doctor.getName(),
                doctor.getLastName(),
                doctor.getLicenseNumber(),
                specialtyResponseDTO
        );
    }

    public List<DoctorResponseDTO> getAllDoctors() {
        List<Doctor> doctors = doctorRepository.findAll();

        List<DoctorResponseDTO> doctorsResponse = new ArrayList<>();

        for (Doctor doctor : doctors) {
            Specialty specialty = doctor.getSpecialty();

            SpecialtyResponseDTO especialidadResponse = new SpecialtyResponseDTO(
                    specialty.getId(),
                    specialty.getName()
            );

            DoctorResponseDTO doctorResponse = new DoctorResponseDTO(
                    doctor.getId(),
                    doctor.getName(),
                    doctor.getLastName(),
                    doctor.getLicenseNumber(),
                    especialidadResponse
            );

            doctorsResponse.add(doctorResponse);
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

        SpecialtyResponseDTO specialtyResponseDTO = new SpecialtyResponseDTO(
                specialty.getId(),
                specialty.getName()
        );

        Doctor newDoctor = new Doctor();

        newDoctor.setName(doctorRequestDTO.name());
        newDoctor.setLastName(doctorRequestDTO.lastName());
        newDoctor.setLicenseNumber(doctorRequestDTO.licenseNumber());
        newDoctor.setSpecialty(specialty);

        newDoctor = doctorRepository.save(newDoctor);

        return new DoctorResponseDTO(
                newDoctor.getId(),
                newDoctor.getName(),
                newDoctor.getLastName(),
                newDoctor.getLicenseNumber(),
                specialtyResponseDTO
        );
    }

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

        Doctor newDoctor = doctorRepository.save(doctor);

        SpecialtyResponseDTO specialtyResponse = new SpecialtyResponseDTO(
                specialty.getId(),
                specialty.getName()
        );

        return new DoctorResponseDTO(
                newDoctor.getId(),
                newDoctor.getName(),
                newDoctor.getLastName(),
                newDoctor.getLicenseNumber(),
                specialtyResponse
        );
    }

    public void deleteDoctor(Long id) {
        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Medico no encontrado"));

        doctorRepository.delete(doctor);
    }
}
