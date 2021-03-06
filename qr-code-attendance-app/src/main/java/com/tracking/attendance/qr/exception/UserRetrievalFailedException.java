package com.tracking.attendance.qr.exception;

import org.springframework.http.HttpStatus;

public class UserRetrievalFailedException extends ApiRequestException {
    public UserRetrievalFailedException() {
        super("Failed to retrieve user.");
    }

    public UserRetrievalFailedException(String message) {
        super(message);
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.BAD_REQUEST;
    }
}
