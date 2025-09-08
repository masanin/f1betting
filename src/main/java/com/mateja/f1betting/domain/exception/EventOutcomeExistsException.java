package com.mateja.f1betting.domain.exception;

public class EventOutcomeExistsException extends RuntimeException {
    public EventOutcomeExistsException(final String message) {
        super(message);
    }
}
