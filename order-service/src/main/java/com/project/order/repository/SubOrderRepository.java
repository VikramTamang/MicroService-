package com.project.order.repository;

import com.project.order.entity.SubOrder;
import com.project.order.entity.SubOrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubOrderRepository extends JpaRepository<SubOrder, Long> {
    Optional<SubOrder> findBySubOrderNumber(String subOrderNumber);
    Page<SubOrder> findBySellerId(Long sellerId, Pageable pageable);
    List<SubOrder> findBySellerIdAndStatus(Long sellerId, SubOrderStatus status);
    List<SubOrder> findByParentOrderId(Long parentOrderId);
}
