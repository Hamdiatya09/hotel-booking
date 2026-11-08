package com.achek.hotelbooking.exception;

/** State conflict (room already booked...) -> HTTP 409. */
public class ConflictException extends RuntimeException {
    public ConflictException(String message) {
        super(message);
    }
}
