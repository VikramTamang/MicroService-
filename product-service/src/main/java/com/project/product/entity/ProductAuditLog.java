package com.project.product.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "product_audit_logs")
public class ProductAuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    private Long actorId;

    @Column(nullable = false, length = 50)
    private String actorRole;

    @Column(nullable = false, length = 50)
    private String action;

    @Enumerated(EnumType.STRING)
    @Column(length = 30)
    private ProductStatus previousStatus;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ProductStatus newStatus;

    @Column(columnDefinition = "TEXT")
    private String reason;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    public ProductAuditLog() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }

    public Long getActorId() { return actorId; }
    public void setActorId(Long actorId) { this.actorId = actorId; }

    public String getActorRole() { return actorRole; }
    public void setActorRole(String actorRole) { this.actorRole = actorRole; }

    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }

    public ProductStatus getPreviousStatus() { return previousStatus; }
    public void setPreviousStatus(ProductStatus previousStatus) { this.previousStatus = previousStatus; }

    public ProductStatus getNewStatus() { return newStatus; }
    public void setNewStatus(ProductStatus newStatus) { this.newStatus = newStatus; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private final ProductAuditLog log = new ProductAuditLog();

        public Builder id(Long id) { log.setId(id); return this; }
        public Builder product(Product product) { log.setProduct(product); return this; }
        public Builder actorId(Long actorId) { log.setActorId(actorId); return this; }
        public Builder actorRole(String actorRole) { log.setActorRole(actorRole); return this; }
        public Builder action(String action) { log.setAction(action); return this; }
        public Builder previousStatus(ProductStatus previousStatus) { log.setPreviousStatus(previousStatus); return this; }
        public Builder newStatus(ProductStatus newStatus) { log.setNewStatus(newStatus); return this; }
        public Builder reason(String reason) { log.setReason(reason); return this; }

        public ProductAuditLog build() { return log; }
    }
}
