package com.mateja.f1betting.domain.exception;

public class DriverMarketNotFoundException extends RuntimeException {
    public DriverMarketNotFoundException(final Throwable exception) {
        super(exception);
    }
}
