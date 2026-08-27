package com.project.order.dto;

import com.project.order.entity.SubOrderStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class SubOrderDto {
    private Long id;
    private Long parentOrderId;
    private String orderNumber;
    private String subOrderNumber;
    private Long sellerId;
    private String sellerStoreName;
    private BigDecimal subtotal;
    private BigDecimal shippingFee;
    private BigDecimal tax;
    private SubOrderStatus status;
    private String cancellationReason;
    private String returnReason;
    private String carrier;
    private String trackingCode;
    private LocalDateTime deliveredAt;
    private List<SubOrderItemDto> items;
    private LocalDateTime createdAt;

    public SubOrderDto() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getParentOrderId() { return parentOrderId; }
    public void setParentOrderId(Long parentOrderId) { this.parentOrderId = parentOrderId; }

    public String getOrderNumber() { return orderNumber; }
    public void setOrderNumber(String orderNumber) { this.orderNumber = orderNumber; }

    public String getSubOrderNumber() { return subOrderNumber; }
    public void setSubOrderNumber(String subOrderNumber) { this.subOrderNumber = subOrderNumber; }

    public Long getSellerId() { return sellerId; }
    public void setSellerId(Long sellerId) { this.sellerId = sellerId; }

    public String getSellerStoreName() { return sellerStoreName; }
    public void setSellerStoreName(String sellerStoreName) { this.sellerStoreName = sellerStoreName; }

    public BigDecimal getSubtotal() { return subtotal; }
    public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }

    public BigDecimal getShippingFee() { return shippingFee; }
    public void setShippingFee(BigDecimal shippingFee) { this.shippingFee = shippingFee; }

    public BigDecimal getTax() { return tax; }
    public void setTax(BigDecimal tax) { this.tax = tax; }

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

    public List<SubOrderItemDto> getItems() { return items; }
    public void setItems(List<SubOrderItemDto> items) { this.items = items; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private final SubOrderDto dto = new SubOrderDto();

        public Builder id(Long id) { dto.setId(id); return this; }
        public Builder parentOrderId(Long parentOrderId) { dto.setParentOrderId(parentOrderId); return this; }
        public Builder orderNumber(String orderNumber) { dto.setOrderNumber(orderNumber); return this; }
        public Builder subOrderNumber(String subOrderNumber) { dto.setSubOrderNumber(subOrderNumber); return this; }
        public Builder sellerId(Long sellerId) { dto.setSellerId(sellerId); return this; }
        public Builder sellerStoreName(String sellerStoreName) { dto.setSellerStoreName(sellerStoreName); return this; }
        public Builder subtotal(BigDecimal subtotal) { dto.setSubtotal(subtotal); return this; }
        public Builder shippingFee(BigDecimal shippingFee) { dto.setShippingFee(shippingFee); return this; }
        public Builder tax(BigDecimal tax) { dto.setTax(tax); return this; }
        public Builder status(SubOrderStatus status) { dto.setStatus(status); return this; }
        public Builder cancellationReason(String cancellationReason) { dto.setCancellationReason(cancellationReason); return this; }
        public Builder returnReason(String returnReason) { dto.setReturnReason(returnReason); return this; }
        public Builder carrier(String carrier) { dto.setCarrier(carrier); return this; }
        public Builder trackingCode(String trackingCode) { dto.setTrackingCode(trackingCode); return this; }
        public Builder deliveredAt(LocalDateTime deliveredAt) { dto.setDeliveredAt(deliveredAt); return this; }
        public Builder items(List<SubOrderItemDto> items) { dto.setItems(items); return this; }
        public Builder createdAt(LocalDateTime createdAt) { dto.setCreatedAt(createdAt); return this; }

        public SubOrderDto build() { return dto; }
    }
}
