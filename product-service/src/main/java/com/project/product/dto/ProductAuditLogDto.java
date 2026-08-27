package com.project.product.dto;

import com.project.product.entity.ProductStatus;
import java.time.LocalDateTime;

public class ProductAuditLogDto {
    private Long id;
    private Long productId;
    private Long actorId;
    private String actorRole;
    private String action;
    private ProductStatus previousStatus;
    private ProductStatus newStatus;
    private String reason;
    private LocalDateTime createdAt;

    public ProductAuditLogDto() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }

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
        private final ProductAuditLogDto dto = new ProductAuditLogDto();

        public Builder id(Long id) { dto.setId(id); return this; }
        public Builder productId(Long productId) { dto.setProductId(productId); return this; }
        public Builder actorId(Long actorId) { dto.setActorId(actorId); return this; }
        public Builder actorRole(String actorRole) { dto.setActorRole(actorRole); return this; }
        public Builder action(String action) { dto.setAction(action); return this; }
        public Builder previousStatus(ProductStatus previousStatus) { dto.setPreviousStatus(previousStatus); return this; }
        public Builder newStatus(ProductStatus newStatus) { dto.setNewStatus(newStatus); return this; }
        public Builder reason(String reason) { dto.setReason(reason); return this; }
        public Builder createdAt(LocalDateTime createdAt) { dto.setCreatedAt(createdAt); return this; }

        public ProductAuditLogDto build() { return dto; }
    }
}
