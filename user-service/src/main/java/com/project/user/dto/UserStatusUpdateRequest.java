package com.project.user.dto;

import com.project.user.entity.UserStatus;
import jakarta.validation.constraints.NotNull;

public class UserStatusUpdateRequest {

    @NotNull(message = "Status is required")
    private UserStatus status;

    private String reason;

    public UserStatusUpdateRequest() {}

    public UserStatusUpdateRequest(UserStatus status, String reason) {
        this.status = status;
        this.reason = reason;
    }

    public UserStatus getStatus() { return status; }
    public void setStatus(UserStatus status) { this.status = status; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private final UserStatusUpdateRequest req = new UserStatusUpdateRequest();
        public Builder status(UserStatus status) { req.setStatus(status); return this; }
        public Builder reason(String reason) { req.setReason(reason); return this; }
        public UserStatusUpdateRequest build() { return req; }
    }
}
