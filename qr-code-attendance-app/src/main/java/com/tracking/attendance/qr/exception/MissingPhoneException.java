package com.tracking.attendance.qr.exception;

import org.springframework.http.HttpStatus;

public class MissingPhoneException extends ApiRequestException {
    public MissingPhoneException() {
        super("User has no phone");
    }

    public MissingPhoneException(String message) {
        super(message);
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.BAD_REQUEST;
    }
}
