package com.project.order.exception;

import org.springframework.http.HttpStatus;

public class AccessDeniedException extends AppException {
    public AccessDeniedException(String message) {
        super(message, HttpStatus.FORBIDDEN, "ACCESS_DENIED");
    }
}
