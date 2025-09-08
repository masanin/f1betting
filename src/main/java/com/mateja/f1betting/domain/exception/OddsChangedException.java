package com.mateja.f1betting.domain.exception;

public class OddsChangedException extends RuntimeException {
    public OddsChangedException(final String message) {
        super(message);
    }
}
