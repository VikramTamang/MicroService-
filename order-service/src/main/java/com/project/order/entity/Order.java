package com.project.order.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 64)
    private String trackingNumber;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false, length = 150)
    private String userEmail;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private OrderStatus status = OrderStatus.CONFIRMED;

    @Column(nullable = false)
    private String shippingAddress;

    @Column(nullable = false, length = 100)
    private String shippingCity;

    @Column(nullable = false, length = 20)
    private String shippingPostalCode;

    @Column(length = 30)
    private String customerPhone;

    @Column(length = 50)
    private String paymentMethod = "CREDIT_CARD";

    @Column(columnDefinition = "TEXT")
    private String notes;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> items = new ArrayList<>();

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public Order() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTrackingNumber() { return trackingNumber; }
    public void setTrackingNumber(String trackingNumber) { this.trackingNumber = trackingNumber; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getUserEmail() { return userEmail; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }

    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }

    public OrderStatus getStatus() { return status; }
    public void setStatus(OrderStatus status) { this.status = status; }

    public String getShippingAddress() { return shippingAddress; }
    public void setShippingAddress(String shippingAddress) { this.shippingAddress = shippingAddress; }

    public String getShippingCity() { return shippingCity; }
    public void setShippingCity(String shippingCity) { this.shippingCity = shippingCity; }

    public String getShippingPostalCode() { return shippingPostalCode; }
    public void setShippingPostalCode(String shippingPostalCode) { this.shippingPostalCode = shippingPostalCode; }

    public String getCustomerPhone() { return customerPhone; }
    public void setCustomerPhone(String customerPhone) { this.customerPhone = customerPhone; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public List<OrderItem> getItems() { return items; }
    public void setItems(List<OrderItem> items) { this.items = items; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public void addItem(OrderItem item) {
        items.add(item);
        item.setOrder(this);
    }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private final Order order = new Order();
        public Builder id(Long id) { order.setId(id); return this; }
        public Builder trackingNumber(String trackingNumber) { order.setTrackingNumber(trackingNumber); return this; }
        public Builder userId(Long userId) { order.setUserId(userId); return this; }
        public Builder userEmail(String userEmail) { order.setUserEmail(userEmail); return this; }
        public Builder totalAmount(BigDecimal totalAmount) { order.setTotalAmount(totalAmount); return this; }
        public Builder status(OrderStatus status) { order.setStatus(status); return this; }
        public Builder shippingAddress(String shippingAddress) { order.setShippingAddress(shippingAddress); return this; }
        public Builder shippingCity(String shippingCity) { order.setShippingCity(shippingCity); return this; }
        public Builder shippingPostalCode(String shippingPostalCode) { order.setShippingPostalCode(shippingPostalCode); return this; }
        public Builder customerPhone(String customerPhone) { order.setCustomerPhone(customerPhone); return this; }
        public Builder paymentMethod(String paymentMethod) { order.setPaymentMethod(paymentMethod); return this; }
        public Builder notes(String notes) { order.setNotes(notes); return this; }
        public Builder items(List<OrderItem> items) { order.setItems(items); return this; }
        public Order build() { return order; }
    }
}
