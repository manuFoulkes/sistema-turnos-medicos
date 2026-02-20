package com.manuelfoulkes.turnos_medicos.services;

import com.manuelfoulkes.turnos_medicos.repositories.TurnoRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class TurnoService {

    private final TurnoRepository turnoRepository;
}
