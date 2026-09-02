package com.project.order.controller;

import com.project.order.dto.*;
import com.project.order.service.MultiSellerOrderService;
import com.project.order.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/orders")
@Tag(name = "Orders", description = "Endpoints for customer order checkout and multi-seller sub-order tracking")
public class OrderController {

    private final MultiSellerOrderService multiSellerOrderService;
    private final OrderService orderService;

    public OrderController(MultiSellerOrderService multiSellerOrderService, OrderService orderService) {
        this.multiSellerOrderService = multiSellerOrderService;
        this.orderService = orderService;
    }

    @PostMapping
    @Operation(summary = "Place a new multi-seller marketplace order")
    public ResponseEntity<ApiResponse<ParentOrderDto>> createOrder(
            @RequestHeader(value = "X-User-Id", required = false) Long authUserId,
            @RequestHeader(value = "X-User-Email", required = false) String authUserEmail,
            @Valid @RequestBody CreateOrderRequest request
    ) {
        if (authUserId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.failure("Authentication required: Missing user identification"));
        }
        String email = (authUserEmail != null && !authUserEmail.isBlank()) ? authUserEmail : "customer@example.com";
        ParentOrderDto parentOrder = multiSellerOrderService.checkout(authUserId, email, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(parentOrder, "Marketplace order placed successfully"));
    }

    @GetMapping("/my-orders")
    @Operation(summary = "Get order history for authenticated customer (with sub-order tracking)")
    public ResponseEntity<ApiResponse<Page<ParentOrderDto>>> getMyOrders(
            @RequestHeader(value = "X-User-Id", required = false) Long authUserId,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "20") int size
    ) {
        if (authUserId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.failure("Authentication required: Missing user identification"));
        }
        Page<ParentOrderDto> orders = multiSellerOrderService.getCustomerOrders(authUserId, page, size);
        return ResponseEntity.ok(ApiResponse.success(orders, "Customer orders retrieved successfully"));
    }

    @GetMapping("/{orderNumber}")
    @Operation(summary = "Get parent order details and split sub-orders by order number")
    public ResponseEntity<ApiResponse<ParentOrderDto>> getOrderByNumber(
            @RequestHeader(value = "X-User-Id", required = false) Long authUserId,
            @PathVariable("orderNumber") String orderNumber
    ) {
        if (authUserId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.failure("Authentication required: Missing user identification"));
        }
        ParentOrderDto order = multiSellerOrderService.getCustomerOrder(orderNumber, authUserId);
        return ResponseEntity.ok(ApiResponse.success(order, "Order details retrieved successfully"));
    }

    @PostMapping("/sub-orders/{subOrderNumber}/cancel")
    @Operation(summary = "Cancel a specific sub-order as customer (only before confirmed by seller)")
    public ResponseEntity<ApiResponse<SubOrderDto>> cancelSubOrder(
            @RequestHeader(value = "X-User-Id", required = false) Long authUserId,
            @PathVariable("subOrderNumber") String subOrderNumber,
            @RequestBody(required = false) CancelSubOrderRequest request
    ) {
        if (authUserId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.failure("Authentication required: Missing user identification"));
        }
        SubOrderDto cancelled = multiSellerOrderService.cancelSubOrderAsCustomer(subOrderNumber, authUserId, request);
        return ResponseEntity.ok(ApiResponse.success(cancelled, "Sub-order cancelled"));
    }
}
