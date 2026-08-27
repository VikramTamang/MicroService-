package com.project.product.controller;

import com.project.product.dto.*;
import com.project.product.service.FileStorageService;
import com.project.product.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
@Tag(name = "Products", description = "Endpoints for browsing, searching, and managing products")
public class ProductController {

    private final ProductService productService;
    private final FileStorageService fileStorageService;

    @GetMapping
    @Operation(summary = "Get paginated products with optional category and search filters")
    public ResponseEntity<ApiResponse<Page<ProductDto>>> getProducts(
            @RequestParam(name = "categoryId", required = false) Long categoryId,
            @RequestParam(name = "search", required = false) String search,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "12") int size,
            @RequestParam(name = "sortBy", defaultValue = "createdAt") String sortBy,
            @RequestParam(name = "sortDir", defaultValue = "desc") String sortDir
    ) {
        Page<ProductDto> products = productService.getPublicProducts(categoryId, search, page, size, sortBy, sortDir);
        return ResponseEntity.ok(ApiResponse.success(products, "Products retrieved successfully"));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get product details by ID")
    public ResponseEntity<ApiResponse<ProductDto>> getProductById(@PathVariable("id") Long id) {
        ProductDto product = productService.getProductById(id);
        return ResponseEntity.ok(ApiResponse.success(product, "Product retrieved successfully"));
    }

    @GetMapping("/slug/{slug}")
    @Operation(summary = "Get product details by slug")
    public ResponseEntity<ApiResponse<ProductDto>> getProductBySlug(@PathVariable("slug") String slug) {
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
            @PathVariable("id") Long id,
            @Valid @RequestBody UpdateProductRequest request
    ) {
        ProductDto product = productService.updateProduct(id, request);
        return ResponseEntity.ok(ApiResponse.success(product, "Product updated successfully"));
    }

    @PatchMapping("/{id}/stock")
    @Operation(summary = "Update stock quantity for a product")
    public ResponseEntity<ApiResponse<ProductDto>> updateStock(
            @PathVariable("id") Long id,
            @Valid @RequestBody StockUpdateRequest request
    ) {
        ProductDto product = productService.updateStock(id, request.getStockQuantity());
        return ResponseEntity.ok(ApiResponse.success(product, "Stock updated successfully"));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Soft-delete a product (Admin)")
    public ResponseEntity<ApiResponse<Void>> deleteProduct(@PathVariable("id") Long id) {
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

    @PostMapping(value = "/upload-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload a product image file via drag & drop or file picker (Admin)")
    public ResponseEntity<ApiResponse<Map<String, String>>> uploadImage(@RequestParam("file") MultipartFile file) {
        String imageUrl = fileStorageService.storeFile(file);
        return ResponseEntity.ok(ApiResponse.success(Map.of("imageUrl", imageUrl), "Image uploaded successfully"));
    }

    @GetMapping("/images/{filename:.+}")
    @Operation(summary = "Serve uploaded product image")
    public ResponseEntity<Resource> serveImage(@PathVariable("filename") String filename) {
        Resource resource = fileStorageService.loadFileAsResource(filename);
        String contentType = fileStorageService.getContentType(filename);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }
}
