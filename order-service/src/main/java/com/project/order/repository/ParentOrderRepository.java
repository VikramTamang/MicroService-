package com.project.order.repository;

import com.project.order.entity.ParentOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ParentOrderRepository extends JpaRepository<ParentOrder, Long> {
    Optional<ParentOrder> findByOrderNumber(String orderNumber);
    Page<ParentOrder> findByCustomerId(Long customerId, Pageable pageable);
}
