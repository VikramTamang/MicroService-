package com.project.product.dto;

import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public class BatchProductRequest {
    @NotEmpty(message = "Product IDs list cannot be empty")
    private List<Long> productIds;

    public BatchProductRequest() {}

    public BatchProductRequest(List<Long> productIds) {
        this.productIds = productIds;
    }

    public List<Long> getProductIds() { return productIds; }
    public void setProductIds(List<Long> productIds) { this.productIds = productIds; }
}
