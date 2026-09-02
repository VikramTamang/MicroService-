package com.project.order.client;

import com.project.order.dto.ApiResponse;
import com.project.order.dto.client.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "product-service")
public interface ProductServiceClient {

    @PostMapping("/api/v1/products/reserve-stock")
    ApiResponse<StockReservationResponse> reserveStock(@RequestBody StockReservationRequest request);

    @PostMapping("/api/v1/products/release-stock")
    ApiResponse<Void> releaseStock(@RequestBody StockReleaseRequest request);

    @PostMapping("/api/v1/products/batch")
    ApiResponse<List<ProductDto>> getProductsBatch(@RequestBody BatchProductRequest request);

    @GetMapping("/api/v1/products/{id}")
    ApiResponse<ProductDto> getProductById(@PathVariable("id") Long id);
}
