package com.project.product.dto;

import java.math.BigDecimal;
import java.util.List;

public class StockReservationResponse {
    private boolean reserved;
    private BigDecimal totalAmount;
    private List<StockReservationItem> confirmedItems;

    public StockReservationResponse() {}

    public boolean isReserved() { return reserved; }
    public void setReserved(boolean reserved) { this.reserved = reserved; }

    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }

    public List<StockReservationItem> getConfirmedItems() { return confirmedItems; }
    public void setConfirmedItems(List<StockReservationItem> confirmedItems) { this.confirmedItems = confirmedItems; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private final StockReservationResponse res = new StockReservationResponse();
        public Builder reserved(boolean reserved) { res.setReserved(reserved); return this; }
        public Builder totalAmount(BigDecimal totalAmount) { res.setTotalAmount(totalAmount); return this; }
        public Builder confirmedItems(List<StockReservationItem> confirmedItems) { res.setConfirmedItems(confirmedItems); return this; }
        public StockReservationResponse build() { return res; }
    }
}
