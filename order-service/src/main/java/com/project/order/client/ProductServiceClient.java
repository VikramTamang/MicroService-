package com.project.order.client;

import com.project.order.dto.ApiResponse;
import com.project.order.dto.client.ProductDto;
import com.project.order.dto.client.StockReservationRequest;
import com.project.order.dto.client.StockReservationResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "product-service")
public interface ProductServiceClient {

    @PostMapping("/api/v1/products/reserve-stock")
    ApiResponse<StockReservationResponse> reserveStock(@RequestBody StockReservationRequest request);

    @GetMapping("/api/v1/products/{id}")
    ApiResponse<ProductDto> getProductById(@PathVariable("id") Long id);
}
