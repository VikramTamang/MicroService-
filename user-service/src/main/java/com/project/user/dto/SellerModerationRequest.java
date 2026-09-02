package com.project.user.dto;

public class SellerModerationRequest {
    private String reason;

    public SellerModerationRequest() {}

    public SellerModerationRequest(String reason) {
        this.reason = reason;
    }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private final SellerModerationRequest req = new SellerModerationRequest();
        public Builder reason(String reason) { req.setReason(reason); return this; }
        public SellerModerationRequest build() { return req; }
    }
}
