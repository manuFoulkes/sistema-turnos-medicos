package com.manuelfoulkes.turnos_medicos.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "turnos")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Turno {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Long id;

    @Column(name = "fecha_hora", nullable = false)
    private LocalDateTime fechaHora;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado")
    private AppointmentStatus estado;

    @ManyToOne
    @JoinColumn(name = "paciente_id", nullable = false)
    private Patient patient;

    @ManyToOne
    @JoinColumn(name = "medico_id", nullable = false)
    private Doctor doctor;

    @Column(updatable = false)
    private LocalDateTime fechaCreacion;

    @PrePersist
    protected void onCreate() {
        fechaCreacion = LocalDateTime.now();
        if (estado == null) {
            estado = AppointmentStatus.RESERVED;
        }
    }
}
