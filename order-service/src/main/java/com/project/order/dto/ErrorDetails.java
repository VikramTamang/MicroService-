package com.project.order.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorDetails {

    private String errorCode;
    private String details;
    private Map<String, String> fieldErrors;

    public ErrorDetails() {}

    public ErrorDetails(String errorCode, String details, Map<String, String> fieldErrors) {
        this.errorCode = errorCode;
        this.details = details;
        this.fieldErrors = fieldErrors;
    }

    public String getErrorCode() { return errorCode; }
    public void setErrorCode(String errorCode) { this.errorCode = errorCode; }

    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }

    public Map<String, String> getFieldErrors() { return fieldErrors; }
    public void setFieldErrors(Map<String, String> fieldErrors) { this.fieldErrors = fieldErrors; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private final ErrorDetails err = new ErrorDetails();
        public Builder errorCode(String errorCode) { err.setErrorCode(errorCode); return this; }
        public Builder details(String details) { err.setDetails(details); return this; }
        public Builder fieldErrors(Map<String, String> fieldErrors) { err.setFieldErrors(fieldErrors); return this; }
        public ErrorDetails build() { return err; }
    }
}
