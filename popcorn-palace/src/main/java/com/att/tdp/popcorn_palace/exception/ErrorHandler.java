package com.att.tdp.popcorn_palace.exception;
import com.att.tdp.popcorn_palace.model.ErrorResponse;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ErrorHandler {

    private static final Map<Class<? extends Exception>, HttpStatus> EXCEPTION_STATUS_MAP = new HashMap<>();

    static {
        EXCEPTION_STATUS_MAP.put(NotFoundException.class, HttpStatus.NOT_FOUND);
        EXCEPTION_STATUS_MAP.put(IllegalArgumentException.class, HttpStatus.BAD_REQUEST);
        EXCEPTION_STATUS_MAP.put(DataIntegrityViolationException.class, HttpStatus.BAD_REQUEST);
        EXCEPTION_STATUS_MAP.put(DateTimeParseException.class, HttpStatus.BAD_REQUEST);
        EXCEPTION_STATUS_MAP.put(RuntimeException.class, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception error) {
        HttpStatus status = EXCEPTION_STATUS_MAP.getOrDefault(error.getClass(), HttpStatus.INTERNAL_SERVER_ERROR);
        ErrorResponse errorResponse = new ErrorResponse(error.getMessage());
        return ResponseEntity.status(status).body(errorResponse);
    }
}