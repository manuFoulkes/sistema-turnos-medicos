package com.manuelfoulkes.turnos_medicos.controllers;

import com.manuelfoulkes.turnos_medicos.dtos.requests.AppointmentRequestDTO;
import com.manuelfoulkes.turnos_medicos.dtos.responses.AppointmentResponseDTO;
import com.manuelfoulkes.turnos_medicos.services.AppointmentService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/appoitments")
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService appointmentService;

    @PostMapping
    public ResponseEntity<AppointmentResponseDTO> bookAppointment(
            @Valid @RequestBody AppointmentRequestDTO request
    ) {
        AppointmentResponseDTO response = appointmentService.bookAppointment(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AppointmentResponseDTO> updateAppointment(
            @PathVariable @Positive Long patientId,
            @PathVariable @Positive Long appointmentId,
            @Valid @RequestBody AppointmentRequestDTO request
    ) {
        AppointmentResponseDTO response = appointmentService.updateAppointment(patientId, appointmentId, request);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AppointmentResponseDTO> completeAppointment(@PathVariable @Positive Long id) {
        AppointmentResponseDTO response = appointmentService.completeAppointment(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AppointmentResponseDTO> cancelAppointment(
            @PathVariable @Positive Long patientId,
            @PathVariable @Positive Long appointmentId
    ) {
        AppointmentResponseDTO response = appointmentService.cancelAppointment(patientId,  appointmentId);
        return ResponseEntity.ok(response);
    }
}
