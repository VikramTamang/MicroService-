package com.project.product.dto;

public class ProductModerationRequest {
    private String reason;

    public ProductModerationRequest() {}

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private final ProductModerationRequest req = new ProductModerationRequest();
        public Builder reason(String reason) { req.setReason(reason); return this; }
        public ProductModerationRequest build() { return req; }
    }
}
