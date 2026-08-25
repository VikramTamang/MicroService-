package com.project.order.dto.client;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockReservationResponse {
    private boolean reserved;
    private BigDecimal totalAmount;
    private List<StockReservationItem> confirmedItems;
    private String failureReason;
}
