package com.manuelfoulkes.turnos_medicos.controllers;

import com.manuelfoulkes.turnos_medicos.dtos.requests.AppointmentRequestDTO;
import com.manuelfoulkes.turnos_medicos.dtos.responses.AppointmentResponseDTO;
import com.manuelfoulkes.turnos_medicos.exceptions.model.ApiError;
import com.manuelfoulkes.turnos_medicos.services.AppointmentService;
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

@Tag(name = "Appointments", description = "Endpoints to manage appointments")
@RestController
@RequestMapping("/api/appointments")
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService appointmentService;

    @Operation(
            summary = "Book an appointment",
            description = "Creates a new appointment for a patient with a doctor"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Appointment booked successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid operation: invalid date, " +
                    "doctor unavailable, or patient appointment limit reached",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "404", description = "Patient or doctor not found",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @PostMapping
    public ResponseEntity<AppointmentResponseDTO> bookAppointment(
            @Valid @RequestBody AppointmentRequestDTO request
    ) {
        AppointmentResponseDTO response = appointmentService.bookAppointment(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(
            summary = "Modify an appointment",
            description = "Allows a patient to modify an appointment"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Appointment updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid operation: date is in the past, " +
                "less than 48h notice, or doctor unavailable",
            content = @Content(schema = @Schema(implementation = ApiError.class))),
        @ApiResponse(responseCode = "404", description = "Resource not found: appointment, " +
                "patient or doctor not found",
            content = @Content(schema = @Schema(implementation = ApiError.class))),
        @ApiResponse(responseCode = "403", description = "Unauthorized operation: appointment " +
                "does not belong to the specific patient",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @PutMapping("/{id}/update")
    public ResponseEntity<AppointmentResponseDTO> updateAppointment(
            @PathVariable @Positive Long id,
            @RequestParam @Positive Long patientId,
            @Valid @RequestBody AppointmentRequestDTO request
    ) {
        AppointmentResponseDTO response = appointmentService.updateAppointment(patientId, id, request);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Complete an appointment",
            description = "Marks an appointment as completed. Can only be done within ±30 minutes of scheduled time"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Appointment completed successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid operation: appointment not " +
                    "in RESERVED status or outside completion window (±30 min)",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "404", description = "Appointment not found",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @PutMapping("/{id}/complete")
    public ResponseEntity<AppointmentResponseDTO> completeAppointment(@PathVariable @Positive Long id) {
        AppointmentResponseDTO response = appointmentService.completeAppointment(id);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Cancel an appointment",
            description = "Allows a patient to cancel their appointment. Requires 48 hours advance notice"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Appointment cancelled successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid operation: less than 48h notice, " +
                    "appointment already completed or cancelled",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "403", description = "Unauthorized: appointment does not belong " +
                    "to the specified patient",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "404", description = "Appointment or patient not found",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @PutMapping("/{id}/cancel")
    public ResponseEntity<AppointmentResponseDTO> cancelAppointment(
            @PathVariable @Positive Long id,
            @RequestParam @Positive Long patientId
    ) {
        AppointmentResponseDTO response = appointmentService.cancelAppointment(patientId, id);
        return ResponseEntity.ok(response);
    }
}
