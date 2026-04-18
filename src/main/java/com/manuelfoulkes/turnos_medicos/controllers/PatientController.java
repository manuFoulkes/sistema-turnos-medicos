package com.manuelfoulkes.turnos_medicos.controllers;

import com.manuelfoulkes.turnos_medicos.dtos.requests.PatientRequestDTO;
import com.manuelfoulkes.turnos_medicos.dtos.responses.PatientResponseDTO;
import com.manuelfoulkes.turnos_medicos.exceptions.model.ApiError;
import com.manuelfoulkes.turnos_medicos.services.PatientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Patients", description = "Endpoints to manage patients information")
@RestController
@RequestMapping("/api/v1/patients")
@RequiredArgsConstructor
public class PatientController {

    private final PatientService patientService;

    @Operation(
            summary = "Get a patient by its ID"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Patient found"),
            @ApiResponse(responseCode = "404", description = "Patient not found",
                content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<PatientResponseDTO> getPatientById(@PathVariable @Positive Long id){
        return ResponseEntity.ok(patientService.getById(id));
    }

    @Operation(
            summary = "Get a list of all patients"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List of all patients")
    })
    @GetMapping
    public ResponseEntity<List<PatientResponseDTO>> getAllPatients(){
        return ResponseEntity.ok(patientService.getAllPatients());
    }

    @Operation(
            summary = "Create a new patient"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Patient created successfully"),
            @ApiResponse(responseCode = "409", description = "Patient has already exists",
                content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @PostMapping
    public ResponseEntity<PatientResponseDTO> savePatient(
            @Valid @RequestBody PatientRequestDTO request
    ){
        PatientResponseDTO response = patientService.createPatient(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(
            summary = "Update the information of a patient"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Patient updated successfully"),
            @ApiResponse(responseCode = "404", description = "Patient not found",
                content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @PutMapping("/{id}")
    public ResponseEntity<PatientResponseDTO> updatePatient(
            @PathVariable @Positive Long id,
            @Valid @RequestBody PatientRequestDTO request
    ) {
        PatientResponseDTO response = patientService.updatePatient(id, request);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Delete a patient from the system"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Patient deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Patient not found",
                content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePatient(@PathVariable @Positive Long id){
        patientService.deletePatient(id);
        return ResponseEntity.noContent().build();
    }
}
