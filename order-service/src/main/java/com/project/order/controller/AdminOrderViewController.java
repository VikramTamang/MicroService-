package com.project.order.controller;

import com.project.order.dto.ApiResponse;
import com.project.order.dto.ParentOrderDto;
import com.project.order.service.MultiSellerOrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin/orders")
@Tag(name = "Admin Orders", description = "Platform-wide view-only access to customer orders and sub-order fulfillment for dispute resolution")
public class AdminOrderViewController {

    private final MultiSellerOrderService multiSellerOrderService;

    public AdminOrderViewController(MultiSellerOrderService multiSellerOrderService) {
        this.multiSellerOrderService = multiSellerOrderService;
    }

    @GetMapping
    @Operation(summary = "Get all marketplace parent orders with sub-order tracking (Admin view-only)")
    public ResponseEntity<ApiResponse<Page<ParentOrderDto>>> getAllOrders(
            @RequestParam(defaultValue = "0", name = "page") int page,
            @RequestParam(defaultValue = "20", name = "size") int size
    ) {
        Page<ParentOrderDto> orders = multiSellerOrderService.getAllOrders(page, size);
        return ResponseEntity.ok(ApiResponse.success(orders, "Marketplace orders retrieved"));
    }
}
