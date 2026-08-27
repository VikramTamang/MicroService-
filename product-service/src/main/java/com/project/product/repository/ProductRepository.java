package com.project.product.repository;

import com.project.product.entity.Product;
import com.project.product.entity.ProductStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    Optional<Product> findBySlug(String slug);

    boolean existsBySlug(String slug);

    Page<Product> findBySellerId(Long sellerId, Pageable pageable);

    List<Product> findBySellerId(Long sellerId);

    Page<Product> findByStatus(ProductStatus status, Pageable pageable);

    List<Product> findByStatus(ProductStatus status);

    @Query("SELECT p FROM Product p WHERE p.active = true AND p.status = 'ACTIVE' " +
           "AND (:categoryId IS NULL OR p.category.id = :categoryId) " +
           "AND (:search IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(p.description) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Product> searchActiveProducts(@Param("categoryId") Long categoryId, @Param("search") String search, Pageable pageable);

    List<Product> findByIdIn(List<Long> ids);
}
