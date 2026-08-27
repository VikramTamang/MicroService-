package com.project.product.dto;

import java.time.LocalDateTime;

public class CategoryDto {
    private Long id;
    private String name;
    private String slug;
    private String description;
    private Integer productCount;
    private LocalDateTime createdAt;

    public CategoryDto() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getSlug() { return slug; }
    public void setSlug(String slug) { this.slug = slug; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Integer getProductCount() { return productCount; }
    public void setProductCount(Integer productCount) { this.productCount = productCount; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private final CategoryDto dto = new CategoryDto();

        public Builder id(Long id) { dto.setId(id); return this; }
        public Builder name(String name) { dto.setName(name); return this; }
        public Builder slug(String slug) { dto.setSlug(slug); return this; }
        public Builder description(String description) { dto.setDescription(description); return this; }
        public Builder productCount(Integer productCount) { dto.setProductCount(productCount); return this; }
        public Builder createdAt(LocalDateTime createdAt) { dto.setCreatedAt(createdAt); return this; }

        public CategoryDto build() { return dto; }
    }
}
