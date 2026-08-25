package com.project.order.dto.client;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockReservationItem {
    private Long productId;
    private Integer quantity;
    private String productName;
    private BigDecimal unitPrice;
    private BigDecimal subtotal;
}
