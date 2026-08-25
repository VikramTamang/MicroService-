package com.project.product.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockReservationRequest {

    private String orderTrackingNumber;

    @NotEmpty(message = "Items list cannot be empty")
    @Valid
    private List<StockReservationItem> items;
}
