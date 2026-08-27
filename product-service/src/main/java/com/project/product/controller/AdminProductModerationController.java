package com.project.product.controller;

import com.project.product.dto.ApiResponse;
import com.project.product.dto.ProductAuditLogDto;
import com.project.product.dto.ProductDto;
import com.project.product.dto.ProductModerationRequest;
import com.project.product.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/products")
@Tag(name = "Admin Product Moderation", description = "Endpoints for platform administrators to review and moderate listings")
public class AdminProductModerationController {

    private final ProductService productService;

    public AdminProductModerationController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    @Operation(summary = "Get all platform products with all lifecycle states (Admin)")
    public ResponseEntity<ApiResponse<Page<ProductDto>>> getAllProducts(
            @RequestParam(defaultValue = "0", name = "page") int page,
            @RequestParam(defaultValue = "20", name = "size") int size
    ) {
        Page<ProductDto> products = productService.getAllProductsForAdmin(page, size);
        return ResponseEntity.ok(ApiResponse.success(products, "All products retrieved"));
    }

    @GetMapping("/pending")
    @Operation(summary = "Get all products awaiting review (Admin)")
    public ResponseEntity<ApiResponse<Page<ProductDto>>> getPendingProducts(
            @RequestParam(defaultValue = "0", name = "page") int page,
            @RequestParam(defaultValue = "20", name = "size") int size
    ) {
        Page<ProductDto> pending = productService.getPendingProducts(page, size);
        return ResponseEntity.ok(ApiResponse.success(pending, "Pending products retrieved"));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Permanently delete/remove a product listing (Admin)")
    public ResponseEntity<ApiResponse<Void>> deleteProduct(@PathVariable("id") Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Product deleted successfully"));
    }

    @PostMapping("/{id}/approve")
    @Operation(summary = "Approve product listing (Admin)")
    public ResponseEntity<ApiResponse<ProductDto>> approveProduct(
            @RequestHeader(value = "X-User-Id", required = false) Long adminId,
            @PathVariable("id") Long id
    ) {
        Long effectiveAdminId = adminId != null ? adminId : 1L;
        ProductDto approved = productService.approveProduct(effectiveAdminId, id);
        return ResponseEntity.ok(ApiResponse.success(approved, "Product listing approved"));
    }

    @PostMapping("/{id}/reject")
    @Operation(summary = "Reject product listing with reason (Admin)")
    public ResponseEntity<ApiResponse<ProductDto>> rejectProduct(
            @RequestHeader(value = "X-User-Id", required = false) Long adminId,
            @PathVariable("id") Long id,
            @RequestBody(required = false) ProductModerationRequest request
    ) {
        Long effectiveAdminId = adminId != null ? adminId : 1L;
        ProductDto rejected = productService.rejectProduct(effectiveAdminId, id, request);
        return ResponseEntity.ok(ApiResponse.success(rejected, "Product listing rejected"));
    }

    @PostMapping("/{id}/suspend")
    @Operation(summary = "Suspend active product listing (Admin)")
    public ResponseEntity<ApiResponse<ProductDto>> suspendProduct(
            @RequestHeader(value = "X-User-Id", required = false) Long adminId,
            @PathVariable("id") Long id,
            @RequestBody(required = false) ProductModerationRequest request
    ) {
        Long effectiveAdminId = adminId != null ? adminId : 1L;
        ProductDto suspended = productService.suspendProduct(effectiveAdminId, id, request);
        return ResponseEntity.ok(ApiResponse.success(suspended, "Product listing suspended"));
    }

    @GetMapping("/{id}/audit-logs")
    @Operation(summary = "Get audit logs for a product (Admin)")
    public ResponseEntity<ApiResponse<List<ProductAuditLogDto>>> getProductAuditLogs(@PathVariable("id") Long id) {
        List<ProductAuditLogDto> logs = productService.getProductAuditLogs(id);
        return ResponseEntity.ok(ApiResponse.success(logs, "Product audit logs retrieved"));
    }
}
