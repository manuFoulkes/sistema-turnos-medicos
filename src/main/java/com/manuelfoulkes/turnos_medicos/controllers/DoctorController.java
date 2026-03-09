package com.manuelfoulkes.turnos_medicos.controllers;

import com.manuelfoulkes.turnos_medicos.dtos.requests.DoctorRequestDTO;
import com.manuelfoulkes.turnos_medicos.dtos.responses.DoctorResponseDTO;
import com.manuelfoulkes.turnos_medicos.exceptions.model.ApiError;
import com.manuelfoulkes.turnos_medicos.services.DoctorService;
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

@Tag(name = "Doctors", description = "Endpoints to manage doctors information")
@RestController
@RequestMapping("/api/doctors")
@RequiredArgsConstructor
public class DoctorController {

    private final DoctorService doctorService;

    @Operation(
            summary = "Get a doctor by its ID"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Doctor found"),
            @ApiResponse(responseCode = "404", description = "Doctor not found",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<DoctorResponseDTO> getDoctor(@PathVariable @Positive Long id){
        return ResponseEntity.ok(doctorService.getDoctorById(id));
    }

    @Operation(
            summary = "Get a list of all doctors"
    )
    @ApiResponse(responseCode = "200", description = "List of all doctors")
    @GetMapping
    public ResponseEntity<List<DoctorResponseDTO>> getAllDoctors() {
        return ResponseEntity.ok(doctorService.getAllDoctors());
    }

    @Operation(
            summary = "Create a doctor"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Doctor created successfully"),
            @ApiResponse(responseCode = "409", description = "Doctor has already exists",
            content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "404", description = "Specialty not found",
            content =  @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @PostMapping
    public ResponseEntity<DoctorResponseDTO> createDoctor(
            @Valid @RequestBody DoctorRequestDTO request
    ){
        DoctorResponseDTO response = doctorService.createDoctor(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(
            summary = "Modify a doctor"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Doctor updated successfully"),
            @ApiResponse(responseCode = "404", description = "Doctor or specialty not found",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @PutMapping("/{id}")
    public ResponseEntity<DoctorResponseDTO> updateDoctor(
        @PathVariable @Positive Long id,
        @Valid @RequestBody DoctorRequestDTO request
    ) {
        DoctorResponseDTO response = doctorService.updateDoctor(id, request);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Delete a doctor"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Doctor deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Doctor not found",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDoctor(@PathVariable @Positive Long id){
        doctorService.deleteDoctor(id);
        return ResponseEntity.noContent().build();
    }
}