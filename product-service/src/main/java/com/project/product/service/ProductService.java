package com.project.product.service;

import com.project.product.dto.*;
import com.project.product.entity.Category;
import com.project.product.entity.Product;
import com.project.product.exception.BadRequestException;
import com.project.product.exception.ResourceNotFoundException;
import com.project.product.repository.CategoryRepository;
import com.project.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    @Transactional(readOnly = true)
    public Page<ProductDto> getProducts(Long categoryId, String search, int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return productRepository.searchProducts(categoryId, search, pageable).map(this::mapToDto);
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

    @Transactional
    public ProductDto createProduct(CreateProductRequest request) {
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
                .name(request.getName().trim())
                .slug(slug)
                .description(request.getDescription())
                .price(request.getPrice())
                .stockQuantity(request.getStockQuantity())
                .category(category)
                .imageUrl(request.getImageUrl())
                .active(true)
                .build();

        Product saved = productRepository.save(product);
        log.info("Created product: id={}, name={}", saved.getId(), saved.getName());
        return mapToDto(saved);
    }

    @Transactional
    public ProductDto updateProduct(Long id, UpdateProductRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));

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
        if (request.getActive() != null) {
            product.setActive(request.getActive());
        }

        Product updated = productRepository.save(product);
        log.info("Updated product: id={}", updated.getId());
        return mapToDto(updated);
    }

    @Transactional
    public ProductDto updateStock(Long id, Integer stockQuantity) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));

        product.setStockQuantity(stockQuantity);
        Product saved = productRepository.save(product);
        log.info("Updated stock for product id={}: new stock={}", id, stockQuantity);
        return mapToDto(saved);
    }

    @Transactional
    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        product.setActive(false); // Soft delete
        productRepository.save(product);
        log.info("Soft-deleted product id: {}", id);
    }

    @Transactional
    public StockReservationResponse verifyAndReserveStock(StockReservationRequest request) {
        log.info("Verifying and reserving stock for order request with {} items", request.getItems().size());
        List<StockReservationItem> confirmedItems = new ArrayList<>();
        BigDecimal totalAmount = BigDecimal.ZERO;

        for (StockReservationItem item : request.getItems()) {
            Product product = productRepository.findById(item.getProductId())
                    .orElseThrow(() -> new BadRequestException("Product not found with id: " + item.getProductId()));

            if (!product.isActive()) {
                throw new BadRequestException("Product is inactive: " + product.getName());
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

    public ProductDto mapToDto(Product product) {
        return ProductDto.builder()
                .id(product.getId())
                .name(product.getName())
                .slug(product.getSlug())
                .description(product.getDescription())
                .price(product.getPrice())
                .stockQuantity(product.getStockQuantity())
                .categoryId(product.getCategory() != null ? product.getCategory().getId() : null)
                .categoryName(product.getCategory() != null ? product.getCategory().getName() : null)
                .imageUrl(product.getImageUrl())
                .active(product.isActive())
                .createdAt(product.getCreatedAt())
                .build();
    }
}
