package com.project.product.service;

import com.project.product.dto.CategoryDto;
import com.project.product.dto.CreateCategoryRequest;
import com.project.product.entity.Category;
import com.project.product.exception.BadRequestException;
import com.project.product.exception.ResourceNotFoundException;
import com.project.product.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.Normalizer;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

@Service
public class CategoryService {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(CategoryService.class);

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }
    private static final Pattern NONLATIN = Pattern.compile("[^\\w-]");
    private static final Pattern WHITESPACE = Pattern.compile("[\\s]");

    @Transactional(readOnly = true)
    public List<CategoryDto> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(this::mapToDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public CategoryDto getCategoryById(Long id) {
        return categoryRepository.findById(id)
                .map(this::mapToDto)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));
    }

    @Transactional
    public CategoryDto createCategory(CreateCategoryRequest request) {
        String slug = slugify(request.getName());
        if (categoryRepository.existsByName(request.getName().trim())) {
            throw new BadRequestException("Category already exists with name: " + request.getName());
        }
        if (categoryRepository.existsBySlug(slug)) {
            slug = slug + "-" + System.currentTimeMillis();
        }

        Category category = Category.builder()
                .name(request.getName().trim())
                .slug(slug)
                .description(request.getDescription())
                .build();

        Category saved = categoryRepository.save(category);
        log.info("Created new category: id={}, name={}", saved.getId(), saved.getName());
        return mapToDto(saved);
    }

    @Transactional
    public void deleteCategory(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new ResourceNotFoundException("Category not found with id: " + id);
        }
        categoryRepository.deleteById(id);
        log.info("Deleted category id: {}", id);
    }

    public CategoryDto mapToDto(Category category) {
        return CategoryDto.builder()
                .id(category.getId())
                .name(category.getName())
                .slug(category.getSlug())
                .description(category.getDescription())
                .productCount(category.getProducts() != null ? category.getProducts().size() : 0)
                .createdAt(category.getCreatedAt())
                .build();
    }

    public static String slugify(String input) {
        if (input == null) return "";
        String nowhitespace = WHITESPACE.matcher(input.trim()).replaceAll("-");
        String normalized = Normalizer.normalize(nowhitespace, Normalizer.Form.NFD);
        String slug = NONLATIN.matcher(normalized).replaceAll("");
        return slug.toLowerCase(Locale.ENGLISH);
    }
}
