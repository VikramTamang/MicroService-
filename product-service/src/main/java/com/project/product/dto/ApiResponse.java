package com.project.product.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    private boolean success;
    private String message;
    private T data;
    private LocalDateTime timestamp = LocalDateTime.now();

    public ApiResponse() {}

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public T getData() { return data; }
    public void setData(T data) { this.data = data; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    public static <T> Builder<T> builder() { return new Builder<>(); }

    public static class Builder<T> {
        private final ApiResponse<T> res = new ApiResponse<>();

        public Builder<T> success(boolean success) { res.setSuccess(success); return this; }
        public Builder<T> message(String message) { res.setMessage(message); return this; }
        public Builder<T> data(T data) { res.setData(data); return this; }
        public Builder<T> timestamp(LocalDateTime timestamp) { res.setTimestamp(timestamp); return this; }

        public ApiResponse<T> build() { return res; }
    }

    public static <T> ApiResponse<T> success(T data, String message) {
        ApiResponse<T> res = new ApiResponse<>();
        res.setSuccess(true);
        res.setMessage(message);
        res.setData(data);
        res.setTimestamp(LocalDateTime.now());
        return res;
    }

    public static <T> ApiResponse<T> success(String message) {
        return success(null, message);
    }

    public static <T> ApiResponse<T> failure(String message) {
        ApiResponse<T> res = new ApiResponse<>();
        res.setSuccess(false);
        res.setMessage(message);
        res.setTimestamp(LocalDateTime.now());
        return res;
    }
}
