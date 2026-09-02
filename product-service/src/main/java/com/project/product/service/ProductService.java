package com.project.product.service;

import com.project.product.dto.*;
import com.project.product.entity.Category;
import com.project.product.entity.Product;
import com.project.product.entity.ProductAuditLog;
import com.project.product.entity.ProductStatus;
import com.project.product.exception.BadRequestException;
import com.project.product.exception.ResourceNotFoundException;
import com.project.product.repository.CategoryRepository;
import com.project.product.repository.ProductAuditLogRepository;
import com.project.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import com.project.product.exception.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class ProductService {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(ProductService.class);

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductAuditLogRepository auditLogRepository;

    public ProductService(
            ProductRepository productRepository,
            CategoryRepository categoryRepository,
            ProductAuditLogRepository auditLogRepository
    ) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.auditLogRepository = auditLogRepository;
    }

    // --- Public Queries (ACTIVE only) ---
    @Transactional(readOnly = true)
    public Page<ProductDto> getPublicProducts(Long categoryId, String search, int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return productRepository.searchActiveProducts(categoryId, search, pageable).map(this::mapToDto);
    }

    @Transactional(readOnly = true)
    public ProductDto getProductById(Long id) {
        return productRepository.findById(id)
                .map(this::mapToDto)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
    }

    @Transactional(readOnly = true)
    public ProductDto getProductBySlug(String slug) {
        return productRepository.findBySlug(slug)
                .map(this::mapToDto)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with slug: " + slug));
    }

    // --- Seller Operations (Scoped by sellerId with BOLA checks) ---
    @Transactional(readOnly = true)
    public Page<ProductDto> getSellerProducts(Long sellerId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return productRepository.findBySellerId(sellerId, pageable).map(this::mapToDto);
    }

    @Transactional
    public ProductDto createSellerProduct(Long sellerId, CreateProductRequest request) {
        String slug = CategoryService.slugify(request.getName());
        if (productRepository.existsBySlug(slug)) {
            slug = slug + "-" + System.currentTimeMillis();
        }

        Category category = null;
        if (request.getCategoryId() != null) {
            category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + request.getCategoryId()));
        }

        Product product = Product.builder()
                .sellerId(sellerId)
                .name(request.getName().trim())
                .slug(slug)
                .sku("SKU-" + System.currentTimeMillis())
                .description(request.getDescription())
                .price(request.getPrice())
                .stockQuantity(request.getStockQuantity())
                .category(category)
                .imageUrl(request.getImageUrl())
                .status(ProductStatus.PENDING_REVIEW)
                .active(true)
                .build();

        Product saved = productRepository.save(product);

        // Record Audit Log
        ProductAuditLog audit = ProductAuditLog.builder()
                .product(saved)
                .actorId(sellerId)
                .actorRole("ROLE_SELLER")
                .action("SUBMIT_FOR_REVIEW")
                .previousStatus(null)
                .newStatus(ProductStatus.PENDING_REVIEW)
                .reason("New product submitted for moderation")
                .build();
        auditLogRepository.save(audit);

        log.info("Seller {} submitted product for review: id={}, name={}", sellerId, saved.getId(), saved.getName());
        return mapToDto(saved);
    }

    @Transactional
    public ProductDto updateSellerProduct(Long sellerId, Long productId, UpdateProductRequest request) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productId));

        // OWASP BOLA check
        if (!product.getSellerId().equals(sellerId)) {
            log.warn("BOLA violation: Seller {} attempted to edit product {} owned by seller {}", sellerId, productId, product.getSellerId());
            throw new AccessDeniedException("Access denied: You do not own this product listing");
        }

        Category category = null;
        if (request.getCategoryId() != null) {
            category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + request.getCategoryId()));
        }

        product.setName(request.getName().trim());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setStockQuantity(request.getStockQuantity());
        product.setCategory(category);
        product.setImageUrl(request.getImageUrl());
        
        // Re-trigger review if product was rejected
        if (product.getStatus() == ProductStatus.REJECTED) {
            product.setStatus(ProductStatus.PENDING_REVIEW);
            product.setRejectionReason(null);
        }

        Product updated = productRepository.save(product);
        return mapToDto(updated);
    }

    @Transactional
    public ProductDto createProduct(CreateProductRequest request) {
        return createSellerProduct(1L, request);
    }

    @Transactional
    public ProductDto updateProduct(Long id, UpdateProductRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        return updateSellerProduct(product.getSellerId(), id, request);
    }

    @Transactional
    public ProductDto updateStock(Long id, Integer stockQuantity) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        return updateSellerStock(product.getSellerId(), id, stockQuantity);
    }

    @Transactional
    public ProductDto updateSellerStock(Long sellerId, Long productId, Integer stockQuantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productId));

        if (!product.getSellerId().equals(sellerId)) {
            throw new AccessDeniedException("Access denied: You do not own this product listing");
        }

        product.setStockQuantity(stockQuantity);
        Product saved = productRepository.save(product);
        log.info("Seller {} updated stock for product {}: {}", sellerId, productId, stockQuantity);
        return mapToDto(saved);
    }

    @Transactional
    public void deleteSellerProduct(Long sellerId, Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productId));

        if (!product.getSellerId().equals(sellerId)) {
            throw new AccessDeniedException("Access denied: You do not own this product listing");
        }

        productRepository.delete(product);
        log.info("Seller {} deleted product {}", sellerId, productId);
    }

    @Transactional
    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        productRepository.delete(product);
        log.info("Deleted product id: {}", id);
    }

    // --- Admin Moderation Operations ---
    @Transactional(readOnly = true)
    public Page<ProductDto> getAllProductsForAdmin(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return productRepository.findAll(pageable).map(this::mapToDto);
    }

    @Transactional(readOnly = true)
    public Page<ProductDto> getPendingProducts(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return productRepository.findByStatus(ProductStatus.PENDING_REVIEW, pageable).map(this::mapToDto);
    }

    @Transactional
    public ProductDto approveProduct(Long adminId, Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productId));

        ProductStatus previous = product.getStatus();
        product.setStatus(ProductStatus.ACTIVE);
        product.setActive(true);
        product.setRejectionReason(null);
        Product saved = productRepository.save(product);

        ProductAuditLog audit = ProductAuditLog.builder()
                .product(saved)
                .actorId(adminId)
                .actorRole("ROLE_ADMIN")
                .action("ADMIN_APPROVE")
                .previousStatus(previous)
                .newStatus(ProductStatus.ACTIVE)
                .reason("Approved by marketplace administrator")
                .build();
        auditLogRepository.save(audit);

        log.info("Admin {} approved product id={}", adminId, productId);
        return mapToDto(saved);
    }

    @Transactional
    public ProductDto rejectProduct(Long adminId, Long productId, ProductModerationRequest request) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productId));

        String reason = (request != null && request.getReason() != null && !request.getReason().isBlank())
                ? request.getReason().trim()
                : "Product listing does not comply with marketplace catalog quality guidelines";

        ProductStatus previous = product.getStatus();
        product.setStatus(ProductStatus.REJECTED);
        product.setRejectionReason(reason);
        Product saved = productRepository.save(product);

        ProductAuditLog audit = ProductAuditLog.builder()
                .product(saved)
                .actorId(adminId)
                .actorRole("ROLE_ADMIN")
                .action("ADMIN_REJECT")
                .previousStatus(previous)
                .newStatus(ProductStatus.REJECTED)
                .reason(reason)
                .build();
        auditLogRepository.save(audit);

        log.info("Admin {} rejected product id={}, reason={}", adminId, productId, reason);
        return mapToDto(saved);
    }

    @Transactional
    public ProductDto suspendProduct(Long adminId, Long productId, ProductModerationRequest request) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productId));

        String reason = (request != null && request.getReason() != null && !request.getReason().isBlank())
                ? request.getReason().trim()
                : "Suspended due to policy violation or counterfeit complaint";

        ProductStatus previous = product.getStatus();
        product.setStatus(ProductStatus.SUSPENDED);
        product.setSuspensionReason(reason);
        Product saved = productRepository.save(product);

        ProductAuditLog audit = ProductAuditLog.builder()
                .product(saved)
                .actorId(adminId)
                .actorRole("ROLE_ADMIN")
                .action("ADMIN_SUSPEND")
                .previousStatus(previous)
                .newStatus(ProductStatus.SUSPENDED)
                .reason(reason)
                .build();
        auditLogRepository.save(audit);

        log.info("Admin {} suspended product id={}, reason={}", adminId, productId, reason);
        return mapToDto(saved);
    }

    @Transactional(readOnly = true)
    public List<ProductAuditLogDto> getProductAuditLogs(Long productId) {
        return auditLogRepository.findByProductIdOrderByCreatedAtDesc(productId).stream()
                .map(log -> ProductAuditLogDto.builder()
                        .id(log.getId())
                        .productId(log.getProduct().getId())
                        .actorId(log.getActorId())
                        .actorRole(log.getActorRole())
                        .action(log.getAction())
                        .previousStatus(log.getPreviousStatus())
                        .newStatus(log.getNewStatus())
                        .reason(log.getReason())
                        .createdAt(log.getCreatedAt())
                        .build())
                .toList();
    }

    // --- Feign Inter-Service Stock Reservation ---
    @Transactional
    public StockReservationResponse verifyAndReserveStock(StockReservationRequest request) {
        log.info("Verifying and reserving stock for order request with {} items", request.getItems().size());
        List<StockReservationItem> confirmedItems = new ArrayList<>();
        BigDecimal totalAmount = BigDecimal.ZERO;

        for (StockReservationItem item : request.getItems()) {
            Product product = productRepository.findById(item.getProductId())
                    .orElseThrow(() -> new BadRequestException("Product not found with id: " + item.getProductId()));

            if (!product.isActive() || product.getStatus() != ProductStatus.ACTIVE) {
                throw new BadRequestException("Product is not active for sale: " + product.getName());
            }

            if (product.getStockQuantity() < item.getQuantity()) {
                throw new BadRequestException("Insufficient stock for product '" + product.getName() + 
                        "'. Requested: " + item.getQuantity() + ", Available: " + product.getStockQuantity());
            }

            // Decrement stock
            product.setStockQuantity(product.getStockQuantity() - item.getQuantity());
            productRepository.save(product);

            BigDecimal itemSubtotal = product.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
            totalAmount = totalAmount.add(itemSubtotal);

            confirmedItems.add(StockReservationItem.builder()
                    .productId(product.getId())
                    .sellerId(product.getSellerId())
                    .sku(product.getSku())
                    .productName(product.getName())
                    .quantity(item.getQuantity())
                    .unitPrice(product.getPrice())
                    .subtotal(itemSubtotal)
                    .build());
        }

        log.info("Successfully reserved stock for all items. Total amount: {}", totalAmount);
        return StockReservationResponse.builder()
                .reserved(true)
                .totalAmount(totalAmount)
                .confirmedItems(confirmedItems)
                .build();
    }

    @Transactional
    public void releaseStock(StockReleaseRequest request) {
        log.info("Compensating stock rollback for tracking: {} with {} items",
                request.getOrderTrackingNumber(), request.getItems() != null ? request.getItems().size() : 0);
        if (request.getItems() == null || request.getItems().isEmpty()) {
            return;
        }
        for (StockReservationItem item : request.getItems()) {
            if (item.getProductId() != null && item.getQuantity() != null && item.getQuantity() > 0) {
                productRepository.findById(item.getProductId()).ifPresent(product -> {
                    product.setStockQuantity(product.getStockQuantity() + item.getQuantity());
                    productRepository.save(product);
                    log.info("Restored stock for product id={} name='{}', incremented by {}, new stock={}",
                            product.getId(), product.getName(), item.getQuantity(), product.getStockQuantity());
                });
            }
        }
    }

    @Transactional(readOnly = true)
    public List<ProductDto> getProductsByIds(List<Long> productIds) {
        if (productIds == null || productIds.isEmpty()) {
            return Collections.emptyList();
        }
        return productRepository.findAllById(productIds).stream()
                .map(this::mapToDto)
                .toList();
    }

    public ProductDto mapToDto(Product product) {
        return ProductDto.builder()
                .id(product.getId())
                .sellerId(product.getSellerId())
                .name(product.getName())
                .slug(product.getSlug())
                .sku(product.getSku())
                .description(product.getDescription())
                .price(product.getPrice())
                .stockQuantity(product.getStockQuantity())
                .categoryId(product.getCategory() != null ? product.getCategory().getId() : null)
                .categoryName(product.getCategory() != null ? product.getCategory().getName() : null)
                .imageUrl(product.getImageUrl())
                .status(product.getStatus())
                .rejectionReason(product.getRejectionReason())
                .suspensionReason(product.getSuspensionReason())
                .active(product.isActive())
                .createdAt(product.getCreatedAt())
                .build();
    }
}
