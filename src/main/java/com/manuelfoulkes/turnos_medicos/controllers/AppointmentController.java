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
@RequestMapping("/api/appointments")
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

    @PutMapping("/{id}/update")
    public ResponseEntity<AppointmentResponseDTO> updateAppointment(
            @PathVariable @Positive Long id,
            @RequestParam @Positive Long patientId,
            @Valid @RequestBody AppointmentRequestDTO request
    ) {
        AppointmentResponseDTO response = appointmentService.updateAppointment(patientId, id, request);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/complete")
    public ResponseEntity<AppointmentResponseDTO> completeAppointment(@PathVariable @Positive Long id) {
        AppointmentResponseDTO response = appointmentService.completeAppointment(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<AppointmentResponseDTO> cancelAppointment(
            @PathVariable @Positive Long id,
            @RequestParam @Positive Long patientId
    ) {
        AppointmentResponseDTO response = appointmentService.cancelAppointment(patientId, id);
        return ResponseEntity.ok(response);
    }
}
