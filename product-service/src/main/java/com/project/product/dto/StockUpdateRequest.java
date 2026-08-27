package com.project.product.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class StockUpdateRequest {
    @NotNull(message = "Stock quantity is required")
    @Min(value = 0, message = "Stock quantity cannot be negative")
    private Integer stockQuantity;

    public StockUpdateRequest() {}

    public Integer getStockQuantity() { return stockQuantity; }
    public void setStockQuantity(Integer stockQuantity) { this.stockQuantity = stockQuantity; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private final StockUpdateRequest req = new StockUpdateRequest();
        public Builder stockQuantity(Integer stockQuantity) { req.setStockQuantity(stockQuantity); return this; }
        public StockUpdateRequest build() { return req; }
    }
}
