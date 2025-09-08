package com.mateja.f1betting.domain.exception;

public class EventOutcomeAlreadyProcessedException extends RuntimeException {
    public EventOutcomeAlreadyProcessedException(final String message) {
        super(message);
    }
}
