package com.tracking.attendance.qr.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ApiException {
    private final LocalDateTime timestamp;
    private final HttpStatus status;
    private final String message;
}
