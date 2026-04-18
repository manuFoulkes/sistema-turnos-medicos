package com.manuelfoulkes.turnos_medicos.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "appointments")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Long id;

    @Column(name = "date_time", nullable = false)
    private LocalDateTime scheduleDateTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private AppointmentStatus status;

    @ManyToOne
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @ManyToOne
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;

    @Column(updatable = false)
    private LocalDateTime creationDate;

    @PrePersist
    protected void onCreate() {
        creationDate = LocalDateTime.now();
        if (status == null) {
            status = AppointmentStatus.RESERVED;
        }
    }
}
