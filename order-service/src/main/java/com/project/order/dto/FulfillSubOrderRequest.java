package com.project.order.dto;

import jakarta.validation.constraints.NotBlank;

public class FulfillSubOrderRequest {

    @NotBlank(message = "Carrier name is required")
    private String carrier;

    @NotBlank(message = "Tracking code is required")
    private String trackingCode;

    public FulfillSubOrderRequest() {}

    public FulfillSubOrderRequest(String carrier, String trackingCode) {
        this.carrier = carrier;
        this.trackingCode = trackingCode;
    }

    public String getCarrier() { return carrier; }
    public void setCarrier(String carrier) { this.carrier = carrier; }

    public String getTrackingCode() { return trackingCode; }
    public void setTrackingCode(String trackingCode) { this.trackingCode = trackingCode; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private final FulfillSubOrderRequest req = new FulfillSubOrderRequest();
        public Builder carrier(String carrier) { req.setCarrier(carrier); return this; }
        public Builder trackingCode(String trackingCode) { req.setTrackingCode(trackingCode); return this; }
        public FulfillSubOrderRequest build() { return req; }
    }
}
