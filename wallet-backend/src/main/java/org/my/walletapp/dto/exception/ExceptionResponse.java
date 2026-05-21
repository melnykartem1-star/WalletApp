package org.my.walletapp.dto.exception;

import java.time.LocalDateTime;

public record ExceptionResponse(
        LocalDateTime timestamp,
        int status,
        String error,
        String message
) {}
