package com.achek.hotelbooking.exception;

/** Business rule violated (invalid dates, overpayment...) -> HTTP 400. */
public class BusinessException extends RuntimeException {
    public BusinessException(String message) {
        super(message);
    }
}
