package com.project.product.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public class CreateProductRequest {
    @NotBlank(message = "Product name is required")
    @Size(max = 200, message = "Product name must not exceed 200 characters")
    private String name;

    private String description;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.01", message = "Price must be greater than 0")
    private BigDecimal price;

    @Min(value = 0, message = "Stock quantity cannot be negative")
    private Integer stockQuantity = 0;

    private Long categoryId;
    private String imageUrl;

    public CreateProductRequest() {}

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public Integer getStockQuantity() { return stockQuantity; }
    public void setStockQuantity(Integer stockQuantity) { this.stockQuantity = stockQuantity; }

    public Long getCategoryId() { return categoryId; }
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private final CreateProductRequest req = new CreateProductRequest();
        public Builder name(String name) { req.setName(name); return this; }
        public Builder description(String description) { req.setDescription(description); return this; }
        public Builder price(BigDecimal price) { req.setPrice(price); return this; }
        public Builder stockQuantity(Integer stockQuantity) { req.setStockQuantity(stockQuantity); return this; }
        public Builder categoryId(Long categoryId) { req.setCategoryId(categoryId); return this; }
        public Builder imageUrl(String imageUrl) { req.setImageUrl(imageUrl); return this; }
        public CreateProductRequest build() { return req; }
    }
}
