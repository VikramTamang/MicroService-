package com.project.order.dto;

public class CancelSubOrderRequest {
    private String reason;

    public CancelSubOrderRequest() {}

    public CancelSubOrderRequest(String reason) {
        this.reason = reason;
    }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private final CancelSubOrderRequest req = new CancelSubOrderRequest();
        public Builder reason(String reason) { req.setReason(reason); return this; }
        public CancelSubOrderRequest build() { return req; }
    }
}
