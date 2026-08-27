package com.project.order.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "parent_orders")
public class ParentOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 64)
    private String orderNumber;

    @Column(nullable = false)
    private Long customerId;

    @Column(nullable = false, length = 150)
    private String customerEmail;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal totalAmount;

    @Column(precision = 10, scale = 2)
    private BigDecimal shippingFee = BigDecimal.ZERO;

    @Column(precision = 10, scale = 2)
    private BigDecimal taxAmount = BigDecimal.ZERO;

    @Column(length = 50)
    private String paymentMethod = "CREDIT_CARD";

    @Column(length = 30)
    private String paymentStatus = "PAID";

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private DerivedOrderStatus derivedStatus = DerivedOrderStatus.PLACED;

    @Column(nullable = false)
    private String shippingAddress;

    private String shippingCity;
    private String shippingPostalCode;

    @OneToMany(mappedBy = "parentOrder", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<SubOrder> subOrders = new ArrayList<>();

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public ParentOrder() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getOrderNumber() { return orderNumber; }
    public void setOrderNumber(String orderNumber) { this.orderNumber = orderNumber; }

    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }

    public String getCustomerEmail() { return customerEmail; }
    public void setCustomerEmail(String customerEmail) { this.customerEmail = customerEmail; }

    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }

    public BigDecimal getShippingFee() { return shippingFee; }
    public void setShippingFee(BigDecimal shippingFee) { this.shippingFee = shippingFee; }

    public BigDecimal getTaxAmount() { return taxAmount; }
    public void setTaxAmount(BigDecimal taxAmount) { this.taxAmount = taxAmount; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }

    public DerivedOrderStatus getDerivedStatus() { return derivedStatus; }
    public void setDerivedStatus(DerivedOrderStatus derivedStatus) { this.derivedStatus = derivedStatus; }

    public String getShippingAddress() { return shippingAddress; }
    public void setShippingAddress(String shippingAddress) { this.shippingAddress = shippingAddress; }

    public String getShippingCity() { return shippingCity; }
    public void setShippingCity(String shippingCity) { this.shippingCity = shippingCity; }

    public String getShippingPostalCode() { return shippingPostalCode; }
    public void setShippingPostalCode(String shippingPostalCode) { this.shippingPostalCode = shippingPostalCode; }

    public List<SubOrder> getSubOrders() { return subOrders; }
    public void setSubOrders(List<SubOrder> subOrders) { this.subOrders = subOrders; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public void addSubOrder(SubOrder subOrder) {
        subOrders.add(subOrder);
        subOrder.setParentOrder(this);
    }

    public void recalculateDerivedStatus() {
        if (subOrders.isEmpty()) {
            this.derivedStatus = DerivedOrderStatus.PLACED;
            return;
        }

        boolean allCancelled = subOrders.stream().allMatch(s -> s.getStatus() == SubOrderStatus.CANCELLED);
        if (allCancelled) {
            this.derivedStatus = DerivedOrderStatus.CANCELLED;
            return;
        }

        boolean allDelivered = subOrders.stream().allMatch(s -> s.getStatus() == SubOrderStatus.DELIVERED);
        if (allDelivered) {
            this.derivedStatus = DerivedOrderStatus.DELIVERED;
            return;
        }

        boolean anyDelivered = subOrders.stream().anyMatch(s -> s.getStatus() == SubOrderStatus.DELIVERED);
        if (anyDelivered) {
            this.derivedStatus = DerivedOrderStatus.PARTIALLY_DELIVERED;
            return;
        }

        boolean allShipped = subOrders.stream().allMatch(s -> s.getStatus() == SubOrderStatus.SHIPPED || s.getStatus() == SubOrderStatus.OUT_FOR_DELIVERY);
        if (allShipped) {
            this.derivedStatus = DerivedOrderStatus.SHIPPED;
            return;
        }

        boolean anyShipped = subOrders.stream().anyMatch(s -> s.getStatus() == SubOrderStatus.SHIPPED || s.getStatus() == SubOrderStatus.OUT_FOR_DELIVERY);
        if (anyShipped) {
            this.derivedStatus = DerivedOrderStatus.PARTIALLY_SHIPPED;
            return;
        }

        boolean anyProcessing = subOrders.stream().anyMatch(s -> s.getStatus() == SubOrderStatus.CONFIRMED || s.getStatus() == SubOrderStatus.PACKED);
        if (anyProcessing) {
            this.derivedStatus = DerivedOrderStatus.PROCESSING;
            return;
        }

        this.derivedStatus = DerivedOrderStatus.PLACED;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final ParentOrder order = new ParentOrder();

        public Builder id(Long id) { order.setId(id); return this; }
        public Builder orderNumber(String orderNumber) { order.setOrderNumber(orderNumber); return this; }
        public Builder customerId(Long customerId) { order.setCustomerId(customerId); return this; }
        public Builder customerEmail(String customerEmail) { order.setCustomerEmail(customerEmail); return this; }
        public Builder totalAmount(BigDecimal totalAmount) { order.setTotalAmount(totalAmount); return this; }
        public Builder shippingFee(BigDecimal shippingFee) { order.setShippingFee(shippingFee); return this; }
        public Builder taxAmount(BigDecimal taxAmount) { order.setTaxAmount(taxAmount); return this; }
        public Builder paymentMethod(String paymentMethod) { order.setPaymentMethod(paymentMethod); return this; }
        public Builder paymentStatus(String paymentStatus) { order.setPaymentStatus(paymentStatus); return this; }
        public Builder derivedStatus(DerivedOrderStatus derivedStatus) { order.setDerivedStatus(derivedStatus); return this; }
        public Builder shippingAddress(String shippingAddress) { order.setShippingAddress(shippingAddress); return this; }
        public Builder shippingCity(String shippingCity) { order.setShippingCity(shippingCity); return this; }
        public Builder shippingPostalCode(String shippingPostalCode) { order.setShippingPostalCode(shippingPostalCode); return this; }
        public Builder subOrders(List<SubOrder> subOrders) { order.setSubOrders(subOrders); return this; }

        public ParentOrder build() { return order; }
    }
}
