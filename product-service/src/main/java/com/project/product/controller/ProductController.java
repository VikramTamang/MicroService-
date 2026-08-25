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
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
@Tag(name = "Products", description = "Endpoints for browsing, searching, and managing products")
public class ProductController {

    private final ProductService productService;

    @GetMapping
    @Operation(summary = "Get paginated products with optional category and search filters")
    public ResponseEntity<ApiResponse<Page<ProductDto>>> getProducts(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir
    ) {
        Page<ProductDto> products = productService.getProducts(categoryId, search, page, size, sortBy, sortDir);
        return ResponseEntity.ok(ApiResponse.success(products, "Products retrieved successfully"));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get product details by ID")
    public ResponseEntity<ApiResponse<ProductDto>> getProductById(@PathVariable Long id) {
        ProductDto product = productService.getProductById(id);
        return ResponseEntity.ok(ApiResponse.success(product, "Product retrieved successfully"));
    }

    @GetMapping("/slug/{slug}")
    @Operation(summary = "Get product details by slug")
    public ResponseEntity<ApiResponse<ProductDto>> getProductBySlug(@PathVariable String slug) {
        ProductDto product = productService.getProductBySlug(slug);
        return ResponseEntity.ok(ApiResponse.success(product, "Product retrieved successfully"));
    }

    @PostMapping
    @Operation(summary = "Create a new product (Admin)")
    public ResponseEntity<ApiResponse<ProductDto>> createProduct(@Valid @RequestBody CreateProductRequest request) {
        ProductDto product = productService.createProduct(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(product, "Product created successfully"));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update product details (Admin)")
    public ResponseEntity<ApiResponse<ProductDto>> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody UpdateProductRequest request
    ) {
        ProductDto product = productService.updateProduct(id, request);
        return ResponseEntity.ok(ApiResponse.success(product, "Product updated successfully"));
    }

    @PatchMapping("/{id}/stock")
    @Operation(summary = "Update stock quantity for a product")
    public ResponseEntity<ApiResponse<ProductDto>> updateStock(
            @PathVariable Long id,
            @Valid @RequestBody StockUpdateRequest request
    ) {
        ProductDto product = productService.updateStock(id, request.getStockQuantity());
        return ResponseEntity.ok(ApiResponse.success(product, "Stock updated successfully"));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Soft-delete a product (Admin)")
    public ResponseEntity<ApiResponse<Void>> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Product deleted successfully"));
    }

    @PostMapping("/reserve-stock")
    @Operation(summary = "Reserve and decrement stock for order placement (Internal inter-service)")
    public ResponseEntity<ApiResponse<StockReservationResponse>> reserveStock(
            @Valid @RequestBody StockReservationRequest request
    ) {
        StockReservationResponse response = productService.verifyAndReserveStock(request);
        return ResponseEntity.ok(ApiResponse.success(response, "Stock reserved successfully"));
    }
}
