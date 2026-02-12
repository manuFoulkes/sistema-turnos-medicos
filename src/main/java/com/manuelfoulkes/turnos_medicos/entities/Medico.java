package com.manuelfoulkes.turnos_medicos.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "medico")
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode
@Builder
public class Medico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Long id;

    @Column(name = "nombre", nullable = false)
    private String nombre;

    @Column(name = "apellido", nullable = false)
    private String apellido;

    @Column(name = "matricula", nullable = false, unique = true)
    private String matricula;

    @ManyToOne
    @JoinColumn(name = "especialidad_id", nullable = false)
    private Especialidad especialidad;
}
