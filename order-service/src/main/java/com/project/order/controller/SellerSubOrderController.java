package com.project.order.controller;

import com.project.order.dto.ApiResponse;
import com.project.order.dto.CancelSubOrderRequest;
import com.project.order.dto.FulfillSubOrderRequest;
import com.project.order.dto.SubOrderDto;
import com.project.order.service.MultiSellerOrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/seller/sub-orders")
@Tag(name = "Seller Orders", description = "Endpoints for sellers to fulfill and manage their own sub-orders")
public class SellerSubOrderController {

    private final MultiSellerOrderService multiSellerOrderService;

    public SellerSubOrderController(MultiSellerOrderService multiSellerOrderService) {
        this.multiSellerOrderService = multiSellerOrderService;
    }

    @GetMapping
    @Operation(summary = "Get all sub-orders assigned to current seller")
    public ResponseEntity<ApiResponse<Page<SubOrderDto>>> getMySubOrders(
            @RequestHeader(value = "X-Seller-Id", required = false) Long sellerId,
            @RequestParam(defaultValue = "0", name = "page") int page,
            @RequestParam(defaultValue = "20", name = "size") int size
    ) {
        Long effectiveSellerId = sellerId != null ? sellerId : 1L;
        Page<SubOrderDto> subOrders = multiSellerOrderService.getSellerSubOrders(effectiveSellerId, page, size);
        return ResponseEntity.ok(ApiResponse.success(subOrders, "Seller sub-orders retrieved"));
    }

    @GetMapping("/{subOrderNumber}")
    @Operation(summary = "Get sub-order details (BOLA protected)")
    public ResponseEntity<ApiResponse<SubOrderDto>> getSubOrderDetails(
            @RequestHeader(value = "X-Seller-Id", required = false) Long sellerId,
            @PathVariable("subOrderNumber") String subOrderNumber
    ) {
        Long effectiveSellerId = sellerId != null ? sellerId : 1L;
        SubOrderDto subOrder = multiSellerOrderService.getSubOrderForSeller(subOrderNumber, effectiveSellerId);
        return ResponseEntity.ok(ApiResponse.success(subOrder, "Sub-order details retrieved"));
    }

    @PostMapping("/{subOrderNumber}/confirm")
    @Operation(summary = "Accept and confirm sub-order")
    public ResponseEntity<ApiResponse<SubOrderDto>> confirmSubOrder(
            @RequestHeader(value = "X-Seller-Id", required = false) Long sellerId,
            @PathVariable("subOrderNumber") String subOrderNumber
    ) {
        Long effectiveSellerId = sellerId != null ? sellerId : 1L;
        SubOrderDto confirmed = multiSellerOrderService.confirmSubOrder(subOrderNumber, effectiveSellerId);
        return ResponseEntity.ok(ApiResponse.success(confirmed, "Sub-order confirmed"));
    }

    @PostMapping("/{subOrderNumber}/pack")
    @Operation(summary = "Mark sub-order items as packed")
    public ResponseEntity<ApiResponse<SubOrderDto>> packSubOrder(
            @RequestHeader(value = "X-Seller-Id", required = false) Long sellerId,
            @PathVariable("subOrderNumber") String subOrderNumber
    ) {
        Long effectiveSellerId = sellerId != null ? sellerId : 1L;
        SubOrderDto packed = multiSellerOrderService.packSubOrder(subOrderNumber, effectiveSellerId);
        return ResponseEntity.ok(ApiResponse.success(packed, "Sub-order marked as packed"));
    }

    @PostMapping("/{subOrderNumber}/ship")
    @Operation(summary = "Ship sub-order with carrier and tracking number")
    public ResponseEntity<ApiResponse<SubOrderDto>> shipSubOrder(
            @RequestHeader(value = "X-Seller-Id", required = false) Long sellerId,
            @PathVariable("subOrderNumber") String subOrderNumber,
            @Valid @RequestBody FulfillSubOrderRequest request
    ) {
        Long effectiveSellerId = sellerId != null ? sellerId : 1L;
        SubOrderDto shipped = multiSellerOrderService.shipSubOrder(subOrderNumber, effectiveSellerId, request);
        return ResponseEntity.ok(ApiResponse.success(shipped, "Sub-order marked as shipped"));
    }

    @PostMapping("/{subOrderNumber}/cancel")
    @Operation(summary = "Cancel sub-order (out of stock)")
    public ResponseEntity<ApiResponse<SubOrderDto>> cancelSubOrder(
            @RequestHeader(value = "X-Seller-Id", required = false) Long sellerId,
            @PathVariable("subOrderNumber") String subOrderNumber,
            @RequestBody(required = false) CancelSubOrderRequest request
    ) {
        Long effectiveSellerId = sellerId != null ? sellerId : 1L;
        SubOrderDto cancelled = multiSellerOrderService.cancelSubOrderAsSeller(subOrderNumber, effectiveSellerId, request);
        return ResponseEntity.ok(ApiResponse.success(cancelled, "Sub-order cancelled"));
    }
}
