package com.project.order.exception;

import com.project.order.dto.ApiResponse;
import com.project.order.dto.ErrorDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(AppException.class)
    public ResponseEntity<ApiResponse<ErrorDetails>> handleAppException(AppException ex) {
        log.error("Order service error: status={}, code={}, message={}", ex.getStatus(), ex.getErrorCode(), ex.getMessage());
        ErrorDetails details = ErrorDetails.builder()
                .errorCode(ex.getErrorCode())
                .details(ex.getMessage())
                .build();
        return ResponseEntity.status(ex.getStatus())
                .body(ApiResponse.<ErrorDetails>builder()
                        .success(false)
                        .message(ex.getMessage())
                        .data(details)
                        .build());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<ErrorDetails>> handleValidationException(MethodArgumentNotValidException ex) {
        Map<String, String> fieldErrors = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            fieldErrors.put(error.getField(), error.getDefaultMessage());
        }

        ErrorDetails details = ErrorDetails.builder()
                .errorCode("VALIDATION_ERROR")
                .details("Input validation failed")
                .fieldErrors(fieldErrors)
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.<ErrorDetails>builder()
                        .success(false)
                        .message("Validation failed")
                        .data(details)
                        .build());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<ErrorDetails>> handleGenericException(Exception ex) {
        log.error("Unhandled order service error: ", ex);
        ErrorDetails details = ErrorDetails.builder()
                .errorCode("INTERNAL_SERVER_ERROR")
                .details(ex.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.<ErrorDetails>builder()
                        .success(false)
                        .message("An unexpected error occurred in order service.")
                        .data(details)
                        .build());
    }
}
