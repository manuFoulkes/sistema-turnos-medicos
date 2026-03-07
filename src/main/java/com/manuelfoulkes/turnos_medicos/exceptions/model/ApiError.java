package com.manuelfoulkes.turnos_medicos.exceptions.model;

import java.time.Instant;

public record ApiError(
        int status,
        String error,
        String message,
        Instant timestamp
) {
}
