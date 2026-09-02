package com.project.order.dto;

import com.project.order.entity.OrderStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class OrderDto {

    private Long id;
    private String trackingNumber;
    private Long userId;
    private String userEmail;
    private BigDecimal totalAmount;
    private OrderStatus status;
    private String shippingAddress;
    private String shippingCity;
    private String shippingPostalCode;
    private String customerPhone;
    private String paymentMethod;
    private String notes;
    private List<OrderItemDto> items;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public OrderDto() {}

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

    public List<OrderItemDto> getItems() { return items; }
    public void setItems(List<OrderItemDto> items) { this.items = items; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private final OrderDto dto = new OrderDto();
        public Builder id(Long id) { dto.setId(id); return this; }
        public Builder trackingNumber(String trackingNumber) { dto.setTrackingNumber(trackingNumber); return this; }
        public Builder userId(Long userId) { dto.setUserId(userId); return this; }
        public Builder userEmail(String userEmail) { dto.setUserEmail(userEmail); return this; }
        public Builder totalAmount(BigDecimal totalAmount) { dto.setTotalAmount(totalAmount); return this; }
        public Builder status(OrderStatus status) { dto.setStatus(status); return this; }
        public Builder shippingAddress(String shippingAddress) { dto.setShippingAddress(shippingAddress); return this; }
        public Builder shippingCity(String shippingCity) { dto.setShippingCity(shippingCity); return this; }
        public Builder shippingPostalCode(String shippingPostalCode) { dto.setShippingPostalCode(shippingPostalCode); return this; }
        public Builder customerPhone(String customerPhone) { dto.setCustomerPhone(customerPhone); return this; }
        public Builder paymentMethod(String paymentMethod) { dto.setPaymentMethod(paymentMethod); return this; }
        public Builder notes(String notes) { dto.setNotes(notes); return this; }
        public Builder items(List<OrderItemDto> items) { dto.setItems(items); return this; }
        public Builder createdAt(LocalDateTime createdAt) { dto.setCreatedAt(createdAt); return this; }
        public Builder updatedAt(LocalDateTime updatedAt) { dto.setUpdatedAt(updatedAt); return this; }
        public OrderDto build() { return dto; }
    }
}
