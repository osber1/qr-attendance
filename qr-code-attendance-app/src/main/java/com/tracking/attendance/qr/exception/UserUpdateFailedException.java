package com.tracking.attendance.qr.exception;

import org.springframework.http.HttpStatus;

public class UserUpdateFailedException extends ApiRequestException {
    public UserUpdateFailedException() {
        super("User update failed.");
    }

    public UserUpdateFailedException(String message) {
        super(message);
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.BAD_REQUEST;
    }
}
