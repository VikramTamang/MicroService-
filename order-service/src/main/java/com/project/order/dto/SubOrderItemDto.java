package com.project.order.dto;

import java.math.BigDecimal;

public class SubOrderItemDto {
    private Long id;
    private Long productId;
    private String productName;
    private String sku;
    private BigDecimal unitPrice;
    private Integer quantity;
    private BigDecimal subtotal;

    public SubOrderItemDto() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public String getSku() { return sku; }
    public void setSku(String sku) { this.sku = sku; }

    public BigDecimal getUnitPrice() { return unitPrice; }
    public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public BigDecimal getSubtotal() { return subtotal; }
    public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private final SubOrderItemDto dto = new SubOrderItemDto();

        public Builder id(Long id) { dto.setId(id); return this; }
        public Builder productId(Long productId) { dto.setProductId(productId); return this; }
        public Builder productName(String productName) { dto.setProductName(productName); return this; }
        public Builder sku(String sku) { dto.setSku(sku); return this; }
        public Builder unitPrice(BigDecimal unitPrice) { dto.setUnitPrice(unitPrice); return this; }
        public Builder quantity(Integer quantity) { dto.setQuantity(quantity); return this; }
        public Builder subtotal(BigDecimal subtotal) { dto.setSubtotal(subtotal); return this; }

        public SubOrderItemDto build() { return dto; }
    }
}
