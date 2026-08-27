package com.project.order.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FulfillSubOrderRequest {

    @NotBlank(message = "Carrier name is required")
    private String carrier;

    @NotBlank(message = "Tracking code is required")
    private String trackingCode;
}
