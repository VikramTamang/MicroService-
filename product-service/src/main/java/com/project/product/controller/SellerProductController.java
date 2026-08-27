package com.project.product.controller;

import com.project.product.dto.*;
import com.project.product.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/seller/products")
@Tag(name = "Seller Products", description = "Endpoints for sellers to manage their own product listings")
public class SellerProductController {

    private final ProductService productService;

    public SellerProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    @Operation(summary = "Get all products owned by current seller")
    public ResponseEntity<ApiResponse<Page<ProductDto>>> getMyProducts(
            @RequestHeader(value = "X-Seller-Id", required = false) Long sellerId,
            @RequestParam(defaultValue = "0", name = "page") int page,
            @RequestParam(defaultValue = "20", name = "size") int size
    ) {
        Long effectiveSellerId = sellerId != null ? sellerId : 1L;
        Page<ProductDto> products = productService.getSellerProducts(effectiveSellerId, page, size);
        return ResponseEntity.ok(ApiResponse.success(products, "Seller products retrieved"));
    }

    @PostMapping
    @Operation(summary = "Create product listing (defaults to PENDING_REVIEW)")
    public ResponseEntity<ApiResponse<ProductDto>> createProduct(
            @RequestHeader(value = "X-Seller-Id", required = false) Long sellerId,
            @Valid @RequestBody CreateProductRequest request
    ) {
        Long effectiveSellerId = sellerId != null ? sellerId : 1L;
        ProductDto created = productService.createSellerProduct(effectiveSellerId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(created, "Product submitted for review"));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update product listing (BOLA protected)")
    public ResponseEntity<ApiResponse<ProductDto>> updateProduct(
            @RequestHeader(value = "X-Seller-Id", required = false) Long sellerId,
            @PathVariable("id") Long id,
            @Valid @RequestBody UpdateProductRequest request
    ) {
        Long effectiveSellerId = sellerId != null ? sellerId : 1L;
        ProductDto updated = productService.updateSellerProduct(effectiveSellerId, id, request);
        return ResponseEntity.ok(ApiResponse.success(updated, "Product updated successfully"));
    }

    @PatchMapping("/{id}/stock")
    @Operation(summary = "Update product stock quantity")
    public ResponseEntity<ApiResponse<ProductDto>> updateStock(
            @RequestHeader(value = "X-Seller-Id", required = false) Long sellerId,
            @PathVariable("id") Long id,
            @Valid @RequestBody StockUpdateRequest request
    ) {
        Long effectiveSellerId = sellerId != null ? sellerId : 1L;
        ProductDto updated = productService.updateSellerStock(effectiveSellerId, id, request.getStockQuantity());
        return ResponseEntity.ok(ApiResponse.success(updated, "Stock quantity updated"));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deactivate product listing")
    public ResponseEntity<ApiResponse<Void>> deleteProduct(
            @RequestHeader(value = "X-Seller-Id", required = false) Long sellerId,
            @PathVariable("id") Long id
    ) {
        Long effectiveSellerId = sellerId != null ? sellerId : 1L;
        productService.deleteSellerProduct(effectiveSellerId, id);
        return ResponseEntity.ok(ApiResponse.success(null, "Product listing deactivated"));
    }
}
