package com.project.order.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public class CreateOrderRequest {

    private Long userId;
    private String userEmail;

    @NotEmpty(message = "Order must contain at least one item")
    @Valid
    private List<OrderItemRequest> items;

    @NotBlank(message = "Shipping address is required")
    private String shippingAddress;

    @NotBlank(message = "Shipping city is required")
    private String shippingCity;

    @NotBlank(message = "Postal code is required")
    private String shippingPostalCode;

    private String customerPhone;
    private String paymentMethod;
    private String notes;

    public CreateOrderRequest() {}

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getUserEmail() { return userEmail; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }

    public List<OrderItemRequest> getItems() { return items; }
    public void setItems(List<OrderItemRequest> items) { this.items = items; }

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

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private final CreateOrderRequest req = new CreateOrderRequest();
        public Builder userId(Long userId) { req.setUserId(userId); return this; }
        public Builder userEmail(String userEmail) { req.setUserEmail(userEmail); return this; }
        public Builder items(List<OrderItemRequest> items) { req.setItems(items); return this; }
        public Builder shippingAddress(String shippingAddress) { req.setShippingAddress(shippingAddress); return this; }
        public Builder shippingCity(String shippingCity) { req.setShippingCity(shippingCity); return this; }
        public Builder shippingPostalCode(String shippingPostalCode) { req.setShippingPostalCode(shippingPostalCode); return this; }
        public Builder customerPhone(String customerPhone) { req.setCustomerPhone(customerPhone); return this; }
        public Builder paymentMethod(String paymentMethod) { req.setPaymentMethod(paymentMethod); return this; }
        public Builder notes(String notes) { req.setNotes(notes); return this; }
        public CreateOrderRequest build() { return req; }
    }
}
