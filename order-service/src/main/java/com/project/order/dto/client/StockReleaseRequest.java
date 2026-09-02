package com.project.order.dto.client;

import java.util.List;

public class StockReleaseRequest {
    private String orderTrackingNumber;
    private List<StockReservationItem> items;

    public StockReleaseRequest() {}

    public StockReleaseRequest(String orderTrackingNumber, List<StockReservationItem> items) {
        this.orderTrackingNumber = orderTrackingNumber;
        this.items = items;
    }

    public String getOrderTrackingNumber() { return orderTrackingNumber; }
    public void setOrderTrackingNumber(String orderTrackingNumber) { this.orderTrackingNumber = orderTrackingNumber; }

    public List<StockReservationItem> getItems() { return items; }
    public void setItems(List<StockReservationItem> items) { this.items = items; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private final StockReleaseRequest req = new StockReleaseRequest();
        public Builder orderTrackingNumber(String orderTrackingNumber) { req.setOrderTrackingNumber(orderTrackingNumber); return this; }
        public Builder items(List<StockReservationItem> items) { req.setItems(items); return this; }
        public StockReleaseRequest build() { return req; }
    }
}
