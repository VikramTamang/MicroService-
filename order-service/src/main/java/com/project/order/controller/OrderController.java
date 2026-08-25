package com.project.order.controller;

import com.project.order.dto.ApiResponse;
import com.project.order.dto.CreateOrderRequest;
import com.project.order.dto.OrderDto;
import com.project.order.dto.OrderStatusUpdateRequest;
import com.project.order.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
@Tag(name = "Orders", description = "Endpoints for order creation, tracking, and management")
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    @Operation(summary = "Place a new order")
    public ResponseEntity<ApiResponse<OrderDto>> createOrder(
            @RequestHeader(value = "X-User-Id", required = false) Long authUserId,
            @RequestHeader(value = "X-User-Email", required = false) String authUserEmail,
            @Valid @RequestBody CreateOrderRequest request
    ) {
        OrderDto orderDto = orderService.createOrder(request, authUserId, authUserEmail);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(orderDto, "Order created successfully"));
    }

    @GetMapping("/my-orders")
    @Operation(summary = "Get order history for authenticated user")
    public ResponseEntity<ApiResponse<List<OrderDto>>> getMyOrders(
            @RequestHeader(value = "X-User-Id", required = false) Long authUserId,
            @RequestHeader(value = "X-User-Email", required = false) String authUserEmail
    ) {
        List<OrderDto> orders = orderService.getOrdersForUser(authUserId, authUserEmail);
        return ResponseEntity.ok(ApiResponse.success(orders, "User orders retrieved successfully"));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get order details by order ID")
    public ResponseEntity<ApiResponse<OrderDto>> getOrderById(@PathVariable Long id) {
        OrderDto order = orderService.getOrderById(id);
        return ResponseEntity.ok(ApiResponse.success(order, "Order details retrieved successfully"));
    }

    @GetMapping("/tracking/{trackingNumber}")
    @Operation(summary = "Get order details by tracking number")
    public ResponseEntity<ApiResponse<OrderDto>> getOrderByTrackingNumber(@PathVariable String trackingNumber) {
        OrderDto order = orderService.getOrderByTrackingNumber(trackingNumber);
        return ResponseEntity.ok(ApiResponse.success(order, "Order details retrieved successfully"));
    }

    @GetMapping
    @Operation(summary = "Get all orders across system (Admin only)")
    public ResponseEntity<ApiResponse<Page<OrderDto>>> getAllOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        Page<OrderDto> orders = orderService.getAllOrders(page, size);
        return ResponseEntity.ok(ApiResponse.success(orders, "All orders retrieved successfully"));
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Update status of an existing order (Admin)")
    public ResponseEntity<ApiResponse<OrderDto>> updateOrderStatus(
            @PathVariable Long id,
            @Valid @RequestBody OrderStatusUpdateRequest request
    ) {
        OrderDto updated = orderService.updateOrderStatus(id, request.getStatus());
        return ResponseEntity.ok(ApiResponse.success(updated, "Order status updated successfully"));
    }
}
