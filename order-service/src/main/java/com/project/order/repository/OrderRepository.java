package com.project.order.repository;

import com.project.order.entity.Order;
import com.project.order.entity.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    Optional<Order> findByTrackingNumber(String trackingNumber);

    List<Order> findByUserIdOrderByCreatedAtDesc(Long userId);

    List<Order> findByUserEmailOrderByCreatedAtDesc(String userEmail);

    Page<Order> findByStatus(OrderStatus status, Pageable pageable);
}
