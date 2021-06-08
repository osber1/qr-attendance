package com.tracking.attendance.qr.exception;

import org.springframework.http.HttpStatus;

public abstract class ApiRequestException extends RuntimeException {
    protected ApiRequestException(String message) {
        super(message);
    }

    public abstract HttpStatus getHttpStatus();
}
