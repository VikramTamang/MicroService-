package com.project.product.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public class StockReservationRequest {
    @NotEmpty(message = "Order must contain at least one item")
    @Valid
    private List<StockReservationItem> items;

    public StockReservationRequest() {}

    public List<StockReservationItem> getItems() { return items; }
    public void setItems(List<StockReservationItem> items) { this.items = items; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private final StockReservationRequest req = new StockReservationRequest();
        public Builder items(List<StockReservationItem> items) { req.setItems(items); return this; }
        public StockReservationRequest build() { return req; }
    }
}
