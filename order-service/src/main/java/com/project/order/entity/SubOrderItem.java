package com.project.order.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "sub_order_items")
public class SubOrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sub_order_id", nullable = false)
    private SubOrder subOrder;

    @Column(nullable = false)
    private Long productId;

    @Column(nullable = false)
    private String productName;

    private String sku;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal unitPrice;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal subtotal;

    public SubOrderItem() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public SubOrder getSubOrder() { return subOrder; }
    public void setSubOrder(SubOrder subOrder) { this.subOrder = subOrder; }

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
        private final SubOrderItem item = new SubOrderItem();

        public Builder id(Long id) { item.setId(id); return this; }
        public Builder subOrder(SubOrder subOrder) { item.setSubOrder(subOrder); return this; }
        public Builder productId(Long productId) { item.setProductId(productId); return this; }
        public Builder productName(String productName) { item.setProductName(productName); return this; }
        public Builder sku(String sku) { item.setSku(sku); return this; }
        public Builder unitPrice(BigDecimal unitPrice) { item.setUnitPrice(unitPrice); return this; }
        public Builder quantity(Integer quantity) { item.setQuantity(quantity); return this; }
        public Builder subtotal(BigDecimal subtotal) { item.setSubtotal(subtotal); return this; }

        public SubOrderItem build() { return item; }
    }
}
