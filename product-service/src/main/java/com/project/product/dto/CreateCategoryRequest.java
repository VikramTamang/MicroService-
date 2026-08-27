package com.project.product.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CreateCategoryRequest {
    @NotBlank(message = "Category name is required")
    @Size(max = 100, message = "Category name must not exceed 100 characters")
    private String name;

    private String description;

    public CreateCategoryRequest() {}

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private final CreateCategoryRequest req = new CreateCategoryRequest();
        public Builder name(String name) { req.setName(name); return this; }
        public Builder description(String description) { req.setDescription(description); return this; }
        public CreateCategoryRequest build() { return req; }
    }
}
