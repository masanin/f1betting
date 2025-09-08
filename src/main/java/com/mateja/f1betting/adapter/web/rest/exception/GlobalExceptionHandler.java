package com.mateja.f1betting.adapter.web.rest.exception;

import com.mateja.f1betting.domain.exception.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import reactor.core.publisher.Mono;

import java.time.Instant;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(InsufficientBalanceException.class)
    public Mono<ProblemDetail> handleInsufficientBalance(final InsufficientBalanceException ex) {
        log.error("Insufficient balance error: {}", ex.getMessage());

        final ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST, ex.getMessage()
        );
        problemDetail.setTitle("Insufficient Balance");
        problemDetail.setProperty("timestamp", Instant.now());

        return Mono.just(problemDetail);
    }

    @ExceptionHandler(DriverMarketNotFoundException.class)
    public Mono<ProblemDetail> handleDriverNotFound(final DriverMarketNotFoundException ex) {
        log.error("Driver Market not found: {}", ex.getMessage());

        final ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.NOT_FOUND, ex.getMessage()
        );
        problemDetail.setTitle("Driver Market Not Found");
        problemDetail.setProperty("timestamp", Instant.now());

        return Mono.just(problemDetail);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public Mono<ProblemDetail> handleUserNotFound(final UserNotFoundException ex) {
        log.error("User not found: {}", ex.getMessage());

        final ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.NOT_FOUND, ex.getMessage()
        );
        problemDetail.setTitle("User Not Found");
        problemDetail.setProperty("timestamp", Instant.now());

        return Mono.just(problemDetail);
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public Mono<ProblemDetail> handleUserAlreadyExists(final UserAlreadyExistsException ex) {
        log.error("User already exists: {}", ex.getMessage());

        final ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.CONFLICT, ex.getMessage()
        );
        problemDetail.setTitle("User Already Exists");
        problemDetail.setProperty("timestamp", Instant.now());

        return Mono.just(problemDetail);
    }

    @ExceptionHandler(EventOutcomeAlreadyProcessedException.class)
    public Mono<ProblemDetail> handleEventOutcomeAlreadyProcessed(final EventOutcomeAlreadyProcessedException ex) {
        log.error("Event outcome already processed: {}", ex.getMessage());

        final ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.CONFLICT, ex.getMessage()
        );
        problemDetail.setTitle("Event Outcome Already Processed");
        problemDetail.setProperty("timestamp", Instant.now());

        return Mono.just(problemDetail);
    }

    @ExceptionHandler(EventOutcomeExistsException.class)
    public Mono<ProblemDetail> handleEventOutcomeExists(final EventOutcomeExistsException ex) {
        log.error("Event outcome exists: {}", ex.getMessage());

        final ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.FORBIDDEN, ex.getMessage()
        );
        problemDetail.setTitle("Event Outcome Exists");
        problemDetail.setProperty("timestamp", Instant.now());

        return Mono.just(problemDetail);
    }

    @ExceptionHandler(WebExchangeBindException.class)
    public Mono<ProblemDetail> handleValidationErrors(final WebExchangeBindException ex) {
        log.error("Validation error: {}", ex.getMessage());

        final ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST, "Validation failed"
        );
        problemDetail.setTitle("Validation Error");

        ex.getBindingResult().getFieldErrors().forEach(error ->
                problemDetail.setProperty(error.getField(), error.getDefaultMessage())
        );

        problemDetail.setProperty("timestamp", Instant.now());

        return Mono.just(problemDetail);
    }

    @ExceptionHandler(Exception.class)
    public Mono<ProblemDetail> handleGenericError(final Exception ex) {
        log.error("Unexpected error: ", ex);

        final ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred"
        );
        problemDetail.setTitle("Internal Server Error");
        problemDetail.setProperty("timestamp", Instant.now());

        return Mono.just(problemDetail);
    }
}
