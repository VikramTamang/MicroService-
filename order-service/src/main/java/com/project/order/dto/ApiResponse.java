package com.project.order.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    private boolean success;
    private String message;
    private T data;
    private LocalDateTime timestamp = LocalDateTime.now();

    public ApiResponse() {}

    public ApiResponse(boolean success, String message, T data, LocalDateTime timestamp) {
        this.success = success;
        this.message = message;
        this.data = data;
        this.timestamp = timestamp != null ? timestamp : LocalDateTime.now();
    }

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public T getData() { return data; }
    public void setData(T data) { this.data = data; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    public static <T> ApiResponse<T> success(T data, String message) {
        ApiResponse<T> resp = new ApiResponse<>();
        resp.setSuccess(true);
        resp.setMessage(message);
        resp.setData(data);
        resp.setTimestamp(LocalDateTime.now());
        return resp;
    }

    public static <T> ApiResponse<T> success(String message) {
        ApiResponse<T> resp = new ApiResponse<>();
        resp.setSuccess(true);
        resp.setMessage(message);
        resp.setTimestamp(LocalDateTime.now());
        return resp;
    }

    public static <T> ApiResponse<T> failure(String message) {
        ApiResponse<T> resp = new ApiResponse<>();
        resp.setSuccess(false);
        resp.setMessage(message);
        resp.setTimestamp(LocalDateTime.now());
        return resp;
    }

    public static <T> Builder<T> builder() { return new Builder<>(); }

    public static class Builder<T> {
        private final ApiResponse<T> resp = new ApiResponse<>();
        public Builder<T> success(boolean success) { resp.setSuccess(success); return this; }
        public Builder<T> message(String message) { resp.setMessage(message); return this; }
        public Builder<T> data(T data) { resp.setData(data); return this; }
        public Builder<T> timestamp(LocalDateTime timestamp) { resp.setTimestamp(timestamp); return this; }
        public ApiResponse<T> build() { return resp; }
    }
}
