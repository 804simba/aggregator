package com.simba.exception;

import com.simba.response.ErrorResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handleMissingServletRequestParameterException(MissingServletRequestParameterException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.builder().success(false).code(HttpStatus.BAD_REQUEST.value())
                        .timestamp(LocalDateTime.now()).message("error").errors(List.of(e.getMessage())).build());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.builder().success(false).code(HttpStatus.BAD_REQUEST.value())
                        .timestamp(LocalDateTime.now()).message("error").errors(List.of(e.getMessage())).build());
    }

    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
                .body(ErrorResponse.builder().success(false).code(HttpStatus.METHOD_NOT_ALLOWED.value())
                        .timestamp(LocalDateTime.now()).message("error").errors(List.of(e.getMessage())).build());
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(AccessDeniedException e) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ErrorResponse.builder().success(false).code(HttpStatus.FORBIDDEN.value())
                        .timestamp(LocalDateTime.now()).message("error").errors(List.of(e.getMessage())).build());
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e) {
        log.error("Exception: ", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ErrorResponse.builder().success(false).code(HttpStatus.INTERNAL_SERVER_ERROR.value()).timestamp(LocalDateTime.now())
                        .message("error").errors(List.of("Something went wrong, please contact app support!")).build());
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorResponse> handleNoResourceFoundException(NoResourceFoundException e) {
        log.error("NoResourceFoundException: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ErrorResponse.builder().success(false).code(HttpStatus.NOT_FOUND.value()).timestamp(LocalDateTime.now())
                        .message("error").errors(List.of("Invalid resource endpoint, check your request")).build());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        String fieldErrors = e.getBindingResult().getFieldErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.joining(", "));

        String globalErrors = e.getBindingResult().getGlobalErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.joining(", "));

        String errorMessage = Stream.of(fieldErrors, globalErrors)
                .filter(message -> !message.isEmpty())
                .collect(Collectors.joining(", "));

        log.error("MethodArgumentNotValidException: {}", errorMessage);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                ErrorResponse.builder()
                        .success(false)
                        .code(HttpStatus.BAD_REQUEST.value())
                        .message("error")
                        .errors(List.of(errorMessage))
                        .timestamp(LocalDateTime.now())
                        .build()
        );
    }


    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(ValidationException e) {
        ErrorResponse errorResponse;
        if (e instanceof ConstraintViolationException) {
            String violations = extractViolationsFromException((ConstraintViolationException) e);
            log.error("ConstraintViolationException: {}", violations);
            errorResponse = ErrorResponse.builder().success(false).code(HttpStatus.BAD_REQUEST.value())
                    .message("error").errors(List.of(violations)).timestamp(LocalDateTime.now()).build();
        } else {
            String exceptionMessage = e.getMessage();
            log.error("ValidationException: {}", exceptionMessage);
            errorResponse = ErrorResponse.builder().success(false).code(HttpStatus.BAD_REQUEST.value())
                    .message("error").errors(List.of(exceptionMessage)).timestamp(LocalDateTime.now()).build();
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    private String extractViolationsFromException(ConstraintViolationException e) {
        return e.getConstraintViolations().stream()
                .map(ConstraintViolation::getMessage).collect(Collectors.joining(", "));
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        log.error("HttpMessageNotReadableException: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ErrorResponse.builder().success(false).code(HttpStatus.BAD_REQUEST.value())
                .message("error").errors(List.of(e.getMessage())).timestamp(LocalDateTime.now()).build());
    }
}
