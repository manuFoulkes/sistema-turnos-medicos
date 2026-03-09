package com.manuelfoulkes.turnos_medicos.controllers;

import com.manuelfoulkes.turnos_medicos.dtos.requests.SpecialtyRequestDTO;
import com.manuelfoulkes.turnos_medicos.dtos.responses.SpecialtyResponseDTO;
import com.manuelfoulkes.turnos_medicos.exceptions.model.ApiError;
import com.manuelfoulkes.turnos_medicos.services.SpecialtyService;
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

@Tag(name = "Specialties", description = "Endpoints to manage specialties")
@RestController
@RequestMapping("/api/specialties")
@RequiredArgsConstructor
public class SpecialtyController {

    private final SpecialtyService specialtyService;

    @Operation(
            summary = "Get a specialty by its ID"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Specialty found"),
            @ApiResponse(responseCode = "404", description = "Specialty not found",
                content = @Content(schema = @Schema(implementation = ApiError.class))),
    })
    @GetMapping("/{id}")
    public ResponseEntity<SpecialtyResponseDTO> getSpecialtyById(@PathVariable Long id){
        return ResponseEntity.ok(specialtyService.getSpecialtyById(id));
    }

    @Operation(
            summary = "Get a list of all specialties"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List of all specialties")
    })
    @GetMapping
    public ResponseEntity<List<SpecialtyResponseDTO>> getSpecialties(){
        return ResponseEntity.ok(specialtyService.getAllSpecialties());
    }

    @Operation(
            summary = "Create a new specialty"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Patient created successfully"),
            @ApiResponse(responseCode = "409", description = "Patient has already exists",
                content = @Content(schema = @Schema(implementation = ApiError.class))),
    })
    @PostMapping
    public ResponseEntity<SpecialtyResponseDTO> createSpecialty(
            @Valid @RequestBody SpecialtyRequestDTO specialtyRequestDTO
    ){
        SpecialtyResponseDTO specialtyResponse = specialtyService.createSpecialty(specialtyRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(specialtyResponse);
    }

    @Operation(
            summary = "Update an specialty"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Specialty updated successfully"),
            @ApiResponse(responseCode = "404", description = "Specialty not found",
                content =  @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @PutMapping("/{id}")
    public ResponseEntity<SpecialtyResponseDTO> updateSpecialty(
            @PathVariable @Positive Long id,
            @Valid @RequestBody SpecialtyRequestDTO specialtyRequest
    ){
        SpecialtyResponseDTO specialtyResponse = specialtyService.updateSpecialty(id, specialtyRequest);
        return ResponseEntity.ok(specialtyResponse);
    }

    @Operation(
            summary = "Delete an specialty"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Specialty deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Specialty not found",
                content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSpecialty(@PathVariable @Positive Long id){
        specialtyService.deleteSpecialty(id);
        return ResponseEntity.noContent().build();
    }
}
