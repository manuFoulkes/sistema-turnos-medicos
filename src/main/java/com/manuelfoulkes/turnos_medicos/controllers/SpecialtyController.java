package com.manuelfoulkes.turnos_medicos.controllers;

import com.manuelfoulkes.turnos_medicos.dtos.requests.SpecialtyRequestDTO;
import com.manuelfoulkes.turnos_medicos.dtos.responses.SpecialtyResponseDTO;
import com.manuelfoulkes.turnos_medicos.services.SpecialtyService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/specialties")
@RequiredArgsConstructor
public class SpecialtyController {

    private final SpecialtyService specialtyService;

    @GetMapping("/{id}")
    public ResponseEntity<SpecialtyResponseDTO> getSpecialtyById(@PathVariable Long id){
        return ResponseEntity.ok(specialtyService.getSpecialtyById(id));
    }

    @GetMapping
    public ResponseEntity<List<SpecialtyResponseDTO>> getSpecialties(){
        return ResponseEntity.ok(specialtyService.getAllSpecialties());
    }

    @PostMapping
    public ResponseEntity<SpecialtyResponseDTO> createSpecialty(
            @Valid @RequestBody SpecialtyRequestDTO specialtyRequestDTO
    ){
        SpecialtyResponseDTO specialtyResponse = specialtyService.createSpecialty(specialtyRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(specialtyResponse);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SpecialtyResponseDTO> updateSpecialty(
            @PathVariable @Positive Long id,
            @Valid @RequestBody SpecialtyRequestDTO specialtyRequest
    ){
        SpecialtyResponseDTO specialtyResponse = specialtyService.updateSpecialty(id, specialtyRequest);
        return ResponseEntity.ok(specialtyResponse);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSpecialty(@PathVariable @Positive Long id){
        specialtyService.deleteSpecialty(id);
        return ResponseEntity.noContent().build();
    }
}
