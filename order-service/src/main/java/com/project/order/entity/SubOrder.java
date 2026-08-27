package com.project.order.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "sub_orders")
public class SubOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_order_id", nullable = false)
    private ParentOrder parentOrder;

    @Column(nullable = false, unique = true, length = 64)
    private String subOrderNumber;

    @Column(nullable = false)
    private Long sellerId;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal subtotal;

    @Column(precision = 10, scale = 2)
    private BigDecimal shippingFee = BigDecimal.valueOf(5.00);

    @Column(precision = 10, scale = 2)
    private BigDecimal tax = BigDecimal.ZERO;

    @Column(precision = 10, scale = 2)
    private BigDecimal commissionAmount = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private SubOrderStatus status = SubOrderStatus.PLACED;

    private String cancellationReason;
    private String returnReason;

    private String carrier;
    private String trackingCode;
    private LocalDateTime deliveredAt;

    @OneToMany(mappedBy = "subOrder", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<SubOrderItem> items = new ArrayList<>();

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public SubOrder() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public ParentOrder getParentOrder() { return parentOrder; }
    public void setParentOrder(ParentOrder parentOrder) { this.parentOrder = parentOrder; }

    public String getSubOrderNumber() { return subOrderNumber; }
    public void setSubOrderNumber(String subOrderNumber) { this.subOrderNumber = subOrderNumber; }

    public Long getSellerId() { return sellerId; }
    public void setSellerId(Long sellerId) { this.sellerId = sellerId; }

    public BigDecimal getSubtotal() { return subtotal; }
    public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }

    public BigDecimal getShippingFee() { return shippingFee; }
    public void setShippingFee(BigDecimal shippingFee) { this.shippingFee = shippingFee; }

    public BigDecimal getTax() { return tax; }
    public void setTax(BigDecimal tax) { this.tax = tax; }

    public BigDecimal getCommissionAmount() { return commissionAmount; }
    public void setCommissionAmount(BigDecimal commissionAmount) { this.commissionAmount = commissionAmount; }

    public SubOrderStatus getStatus() { return status; }
    public void setStatus(SubOrderStatus status) { this.status = status; }

    public String getCancellationReason() { return cancellationReason; }
    public void setCancellationReason(String cancellationReason) { this.cancellationReason = cancellationReason; }

    public String getReturnReason() { return returnReason; }
    public void setReturnReason(String returnReason) { this.returnReason = returnReason; }

    public String getCarrier() { return carrier; }
    public void setCarrier(String carrier) { this.carrier = carrier; }

    public String getTrackingCode() { return trackingCode; }
    public void setTrackingCode(String trackingCode) { this.trackingCode = trackingCode; }

    public LocalDateTime getDeliveredAt() { return deliveredAt; }
    public void setDeliveredAt(LocalDateTime deliveredAt) { this.deliveredAt = deliveredAt; }

    public List<SubOrderItem> getItems() { return items; }
    public void setItems(List<SubOrderItem> items) { this.items = items; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public void addItem(SubOrderItem item) {
        items.add(item);
        item.setSubOrder(this);
    }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private final SubOrder subOrder = new SubOrder();

        public Builder id(Long id) { subOrder.setId(id); return this; }
        public Builder parentOrder(ParentOrder parentOrder) { subOrder.setParentOrder(parentOrder); return this; }
        public Builder subOrderNumber(String subOrderNumber) { subOrder.setSubOrderNumber(subOrderNumber); return this; }
        public Builder sellerId(Long sellerId) { subOrder.setSellerId(sellerId); return this; }
        public Builder subtotal(BigDecimal subtotal) { subOrder.setSubtotal(subtotal); return this; }
        public Builder shippingFee(BigDecimal shippingFee) { subOrder.setShippingFee(shippingFee); return this; }
        public Builder tax(BigDecimal tax) { subOrder.setTax(tax); return this; }
        public Builder commissionAmount(BigDecimal commissionAmount) { subOrder.setCommissionAmount(commissionAmount); return this; }
        public Builder status(SubOrderStatus status) { subOrder.setStatus(status); return this; }
        public Builder items(List<SubOrderItem> items) { subOrder.setItems(items); return this; }

        public SubOrder build() { return subOrder; }
    }
}
