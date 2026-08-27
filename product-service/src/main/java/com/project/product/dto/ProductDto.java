package com.project.product.dto;

import com.project.product.entity.ProductStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ProductDto {
    private Long id;
    private Long sellerId;
    private String name;
    private String slug;
    private String sku;
    private String description;
    private BigDecimal price;
    private Integer stockQuantity;
    private Long categoryId;
    private String categoryName;
    private String imageUrl;
    private ProductStatus status;
    private String rejectionReason;
    private String suspensionReason;
    private boolean active;
    private LocalDateTime createdAt;

    public ProductDto() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getSellerId() { return sellerId; }
    public void setSellerId(Long sellerId) { this.sellerId = sellerId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getSlug() { return slug; }
    public void setSlug(String slug) { this.slug = slug; }

    public String getSku() { return sku; }
    public void setSku(String sku) { this.sku = sku; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public Integer getStockQuantity() { return stockQuantity; }
    public void setStockQuantity(Integer stockQuantity) { this.stockQuantity = stockQuantity; }

    public Long getCategoryId() { return categoryId; }
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }

    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public ProductStatus getStatus() { return status; }
    public void setStatus(ProductStatus status) { this.status = status; }

    public String getRejectionReason() { return rejectionReason; }
    public void setRejectionReason(String rejectionReason) { this.rejectionReason = rejectionReason; }

    public String getSuspensionReason() { return suspensionReason; }
    public void setSuspensionReason(String suspensionReason) { this.suspensionReason = suspensionReason; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private final ProductDto dto = new ProductDto();

        public Builder id(Long id) { dto.setId(id); return this; }
        public Builder sellerId(Long sellerId) { dto.setSellerId(sellerId); return this; }
        public Builder name(String name) { dto.setName(name); return this; }
        public Builder slug(String slug) { dto.setSlug(slug); return this; }
        public Builder sku(String sku) { dto.setSku(sku); return this; }
        public Builder description(String description) { dto.setDescription(description); return this; }
        public Builder price(BigDecimal price) { dto.setPrice(price); return this; }
        public Builder stockQuantity(Integer stockQuantity) { dto.setStockQuantity(stockQuantity); return this; }
        public Builder categoryId(Long categoryId) { dto.setCategoryId(categoryId); return this; }
        public Builder categoryName(String categoryName) { dto.setCategoryName(categoryName); return this; }
        public Builder imageUrl(String imageUrl) { dto.setImageUrl(imageUrl); return this; }
        public Builder status(ProductStatus status) { dto.setStatus(status); return this; }
        public Builder rejectionReason(String rejectionReason) { dto.setRejectionReason(rejectionReason); return this; }
        public Builder suspensionReason(String suspensionReason) { dto.setSuspensionReason(suspensionReason); return this; }
        public Builder active(boolean active) { dto.setActive(active); return this; }
        public Builder createdAt(LocalDateTime createdAt) { dto.setCreatedAt(createdAt); return this; }

        public ProductDto build() { return dto; }
    }
}
