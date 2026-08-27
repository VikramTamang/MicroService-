package com.project.product.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public class StockReservationItem {
    @NotNull(message = "Product ID is required")
    private Long productId;

    private String productName;

    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;

    private BigDecimal unitPrice;
    private BigDecimal subtotal;

    public StockReservationItem() {}

    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public BigDecimal getUnitPrice() { return unitPrice; }
    public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }

    public BigDecimal getSubtotal() { return subtotal; }
    public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private final StockReservationItem item = new StockReservationItem();
        public Builder productId(Long productId) { item.setProductId(productId); return this; }
        public Builder productName(String productName) { item.setProductName(productName); return this; }
        public Builder quantity(Integer quantity) { item.setQuantity(quantity); return this; }
        public Builder unitPrice(BigDecimal unitPrice) { item.setUnitPrice(unitPrice); return this; }
        public Builder subtotal(BigDecimal subtotal) { item.setSubtotal(subtotal); return this; }
        public StockReservationItem build() { return item; }
    }
}
