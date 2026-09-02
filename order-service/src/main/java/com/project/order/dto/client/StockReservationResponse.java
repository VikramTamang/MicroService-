package com.project.order.dto.client;

import java.math.BigDecimal;
import java.util.List;

public class StockReservationResponse {
    private boolean reserved;
    private BigDecimal totalAmount;
    private List<StockReservationItem> confirmedItems;
    private String failureReason;

    public StockReservationResponse() {}

    public StockReservationResponse(boolean reserved, BigDecimal totalAmount, List<StockReservationItem> confirmedItems, String failureReason) {
        this.reserved = reserved;
        this.totalAmount = totalAmount;
        this.confirmedItems = confirmedItems;
        this.failureReason = failureReason;
    }

    public boolean isReserved() { return reserved; }
    public void setReserved(boolean reserved) { this.reserved = reserved; }

    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }

    public List<StockReservationItem> getConfirmedItems() { return confirmedItems; }
    public void setConfirmedItems(List<StockReservationItem> confirmedItems) { this.confirmedItems = confirmedItems; }

    public String getFailureReason() { return failureReason; }
    public void setFailureReason(String failureReason) { this.failureReason = failureReason; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private final StockReservationResponse resp = new StockReservationResponse();
        public Builder reserved(boolean reserved) { resp.setReserved(reserved); return this; }
        public Builder totalAmount(BigDecimal totalAmount) { resp.setTotalAmount(totalAmount); return this; }
        public Builder confirmedItems(List<StockReservationItem> confirmedItems) { resp.setConfirmedItems(confirmedItems); return this; }
        public Builder failureReason(String failureReason) { resp.setFailureReason(failureReason); return this; }
        public StockReservationResponse build() { return resp; }
    }
}
