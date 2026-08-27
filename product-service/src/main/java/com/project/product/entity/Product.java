package com.project.product.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long sellerId = 1L;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(nullable = false, unique = true, length = 220)
    private String slug;

    @Column(length = 100)
    private String sku;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(nullable = false)
    private Integer stockQuantity = 0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @Column(columnDefinition = "TEXT")
    private String imageUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ProductStatus status = ProductStatus.ACTIVE;

    @Column(columnDefinition = "TEXT")
    private String rejectionReason;

    @Column(columnDefinition = "TEXT")
    private String suspensionReason;

    private boolean active = true;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductAuditLog> auditLogs = new ArrayList<>();

    @Version
    private Long version = 0L;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public Product() {}

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

    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }

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

    public List<ProductAuditLog> getAuditLogs() { return auditLogs; }
    public void setAuditLogs(List<ProductAuditLog> auditLogs) { this.auditLogs = auditLogs; }

    public Long getVersion() { return version; }
    public void setVersion(Long version) { this.version = version; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public void addAuditLog(ProductAuditLog log) {
        auditLogs.add(log);
        log.setProduct(this);
    }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private final Product product = new Product();

        public Builder id(Long id) { product.setId(id); return this; }
        public Builder sellerId(Long sellerId) { product.setSellerId(sellerId); return this; }
        public Builder name(String name) { product.setName(name); return this; }
        public Builder slug(String slug) { product.setSlug(slug); return this; }
        public Builder sku(String sku) { product.setSku(sku); return this; }
        public Builder description(String description) { product.setDescription(description); return this; }
        public Builder price(BigDecimal price) { product.setPrice(price); return this; }
        public Builder stockQuantity(Integer stockQuantity) { product.setStockQuantity(stockQuantity); return this; }
        public Builder category(Category category) { product.setCategory(category); return this; }
        public Builder imageUrl(String imageUrl) { product.setImageUrl(imageUrl); return this; }
        public Builder status(ProductStatus status) { product.setStatus(status); return this; }
        public Builder rejectionReason(String rejectionReason) { product.setRejectionReason(rejectionReason); return this; }
        public Builder suspensionReason(String suspensionReason) { product.setSuspensionReason(suspensionReason); return this; }
        public Builder active(boolean active) { product.setActive(active); return this; }
        public Builder version(Long version) { product.setVersion(version); return this; }

        public Product build() { return product; }
    }
}
