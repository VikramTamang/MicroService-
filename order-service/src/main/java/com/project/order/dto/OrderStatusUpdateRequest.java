package com.project.order.dto;

import com.project.order.entity.OrderStatus;
import jakarta.validation.constraints.NotNull;

public class OrderStatusUpdateRequest {

    @NotNull(message = "Status is required")
    private OrderStatus status;

    public OrderStatusUpdateRequest() {}

    public OrderStatusUpdateRequest(OrderStatus status) {
        this.status = status;
    }

    public OrderStatus getStatus() { return status; }
    public void setStatus(OrderStatus status) { this.status = status; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private final OrderStatusUpdateRequest req = new OrderStatusUpdateRequest();
        public Builder status(OrderStatus status) { req.setStatus(status); return this; }
        public OrderStatusUpdateRequest build() { return req; }
    }
}
