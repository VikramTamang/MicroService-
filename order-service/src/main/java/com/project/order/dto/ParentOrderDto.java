package com.project.order.dto;

import com.project.order.entity.DerivedOrderStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class ParentOrderDto {
    private Long id;
    private String orderNumber;
    private Long customerId;
    private String customerEmail;
    private BigDecimal totalAmount;
    private BigDecimal shippingFee;
    private BigDecimal taxAmount;
    private String paymentMethod;
    private String paymentStatus;
    private DerivedOrderStatus derivedStatus;
    private String shippingAddress;
    private String shippingCity;
    private String shippingPostalCode;
    private List<SubOrderDto> subOrders;
    private LocalDateTime createdAt;

    public ParentOrderDto() {}

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

    public List<SubOrderDto> getSubOrders() { return subOrders; }
    public void setSubOrders(List<SubOrderDto> subOrders) { this.subOrders = subOrders; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private final ParentOrderDto dto = new ParentOrderDto();

        public Builder id(Long id) { dto.setId(id); return this; }
        public Builder orderNumber(String orderNumber) { dto.setOrderNumber(orderNumber); return this; }
        public Builder customerId(Long customerId) { dto.setCustomerId(customerId); return this; }
        public Builder customerEmail(String customerEmail) { dto.setCustomerEmail(customerEmail); return this; }
        public Builder totalAmount(BigDecimal totalAmount) { dto.setTotalAmount(totalAmount); return this; }
        public Builder shippingFee(BigDecimal shippingFee) { dto.setShippingFee(shippingFee); return this; }
        public Builder taxAmount(BigDecimal taxAmount) { dto.setTaxAmount(taxAmount); return this; }
        public Builder paymentMethod(String paymentMethod) { dto.setPaymentMethod(paymentMethod); return this; }
        public Builder paymentStatus(String paymentStatus) { dto.setPaymentStatus(paymentStatus); return this; }
        public Builder derivedStatus(DerivedOrderStatus derivedStatus) { dto.setDerivedStatus(derivedStatus); return this; }
        public Builder shippingAddress(String shippingAddress) { dto.setShippingAddress(shippingAddress); return this; }
        public Builder shippingCity(String shippingCity) { dto.setShippingCity(shippingCity); return this; }
        public Builder shippingPostalCode(String shippingPostalCode) { dto.setShippingPostalCode(shippingPostalCode); return this; }
        public Builder subOrders(List<SubOrderDto> subOrders) { dto.setSubOrders(subOrders); return this; }
        public Builder createdAt(LocalDateTime createdAt) { dto.setCreatedAt(createdAt); return this; }

        public ParentOrderDto build() { return dto; }
    }
}
