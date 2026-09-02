package com.project.order.dto.client;

import java.math.BigDecimal;

public class StockReservationItem {
    private Long productId;
    private Long sellerId;
    private String sku;
    private Integer quantity;
    private String productName;
    private BigDecimal unitPrice;
    private BigDecimal subtotal;

    public StockReservationItem() {}

    public StockReservationItem(Long productId, Long sellerId, String sku, Integer quantity, String productName, BigDecimal unitPrice, BigDecimal subtotal) {
        this.productId = productId;
        this.sellerId = sellerId;
        this.sku = sku;
        this.quantity = quantity;
        this.productName = productName;
        this.unitPrice = unitPrice;
        this.subtotal = subtotal;
    }

    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }

    public Long getSellerId() { return sellerId; }
    public void setSellerId(Long sellerId) { this.sellerId = sellerId; }

    public String getSku() { return sku; }
    public void setSku(String sku) { this.sku = sku; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public BigDecimal getUnitPrice() { return unitPrice; }
    public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }

    public BigDecimal getSubtotal() { return subtotal; }
    public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private final StockReservationItem item = new StockReservationItem();
        public Builder productId(Long productId) { item.setProductId(productId); return this; }
        public Builder sellerId(Long sellerId) { item.setSellerId(sellerId); return this; }
        public Builder sku(String sku) { item.setSku(sku); return this; }
        public Builder quantity(Integer quantity) { item.setQuantity(quantity); return this; }
        public Builder productName(String productName) { item.setProductName(productName); return this; }
        public Builder unitPrice(BigDecimal unitPrice) { item.setUnitPrice(unitPrice); return this; }
        public Builder subtotal(BigDecimal subtotal) { item.setSubtotal(subtotal); return this; }
        public StockReservationItem build() { return item; }
    }
}
