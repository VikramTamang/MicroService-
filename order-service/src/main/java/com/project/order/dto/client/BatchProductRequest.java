package com.project.order.dto.client;

import java.util.List;

public class BatchProductRequest {
    private List<Long> productIds;

    public BatchProductRequest() {}

    public BatchProductRequest(List<Long> productIds) {
        this.productIds = productIds;
    }

    public List<Long> getProductIds() { return productIds; }
    public void setProductIds(List<Long> productIds) { this.productIds = productIds; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private final BatchProductRequest req = new BatchProductRequest();
        public Builder productIds(List<Long> productIds) { req.setProductIds(productIds); return this; }
        public BatchProductRequest build() { return req; }
    }
}
