package com.project.order.service;

import com.project.order.client.ProductServiceClient;
import com.project.order.client.UserServiceClient;
import com.project.order.dto.*;
import com.project.order.dto.client.ProductDto;
import com.project.order.dto.client.StockReservationItem;
import com.project.order.dto.client.StockReservationRequest;
import com.project.order.dto.client.StockReservationResponse;
import com.project.order.entity.*;
import com.project.order.exception.BadRequestException;
import com.project.order.exception.ResourceNotFoundException;
import com.project.order.repository.ParentOrderRepository;
import com.project.order.repository.SubOrderItemRepository;
import com.project.order.repository.SubOrderRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import com.project.order.exception.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class MultiSellerOrderService {

    private static final Logger log = LoggerFactory.getLogger(MultiSellerOrderService.class);

    private final ParentOrderRepository parentOrderRepository;
    private final SubOrderRepository subOrderRepository;
    private final SubOrderItemRepository subOrderItemRepository;
    private final ProductServiceClient productServiceClient;
    private final UserServiceClient userServiceClient;

    private static final SecureRandom RANDOM = new SecureRandom();

    public MultiSellerOrderService(
            ParentOrderRepository parentOrderRepository,
            SubOrderRepository subOrderRepository,
            SubOrderItemRepository subOrderItemRepository,
            ProductServiceClient productServiceClient,
            UserServiceClient userServiceClient
    ) {
        this.parentOrderRepository = parentOrderRepository;
        this.subOrderRepository = subOrderRepository;
        this.subOrderItemRepository = subOrderItemRepository;
        this.productServiceClient = productServiceClient;
        this.userServiceClient = userServiceClient;
    }

    // --- Customer Checkout (Multi-Seller Decomposition) ---
    @Transactional
    public ParentOrderDto checkout(Long customerId, String customerEmail, CreateOrderRequest request) {
        log.info("Processing multi-seller checkout for customerId={}, items={}", customerId, request.getItems().size());

        if (request.getItems() == null || request.getItems().isEmpty()) {
            throw new BadRequestException("Order must contain at least one item");
        }

        // 1. Prepare Stock Reservation Feign Call
        List<StockReservationItem> reservationItems = request.getItems().stream()
                .map(item -> StockReservationItem.builder()
                        .productId(item.getProductId())
                        .quantity(item.getQuantity())
                        .build())
                .toList();

        var stockRes = productServiceClient.reserveStock(
                StockReservationRequest.builder().items(reservationItems).build()
        );
        StockReservationResponse stockResponse = stockRes != null ? stockRes.getData() : null;

        if (stockResponse == null || !stockResponse.isReserved()) {
            throw new BadRequestException("Failed to reserve stock for products in cart");
        }

        // 2. Fetch product details to map seller IDs
        Map<Long, ProductDto> productMap = new HashMap<>();
        for (OrderItemRequest itemReq : request.getItems()) {
            try {
                var res = productServiceClient.getProductById(itemReq.getProductId());
                if (res != null && res.getData() != null) {
                    productMap.put(itemReq.getProductId(), res.getData());
                }
            } catch (Exception e) {
                log.warn("Could not fetch product detail for productId={}", itemReq.getProductId(), e);
            }
        }

        // Group cart items by sellerId (default to 1 if not present)
        Map<Long, List<OrderItemRequest>> sellerItemsMap = new LinkedHashMap<>();
        for (OrderItemRequest itemReq : request.getItems()) {
            ProductDto prod = productMap.get(itemReq.getProductId());
            Long sellerId = (prod != null && prod.getSellerId() != null) ? prod.getSellerId() : 1L;
            sellerItemsMap.computeIfAbsent(sellerId, k -> new ArrayList<>()).add(itemReq);
        }

        // 3. Create Parent Order
        String datePrefix = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String parentOrderNumber = "ORD-" + datePrefix + "-" + String.format("%05d", RANDOM.nextInt(100000));

        BigDecimal totalAmount = BigDecimal.ZERO;
        BigDecimal totalShipping = BigDecimal.valueOf(sellerItemsMap.size() * 5.00); // $5 flat per seller shipment

        ParentOrder parentOrder = ParentOrder.builder()
                .orderNumber(parentOrderNumber)
                .customerId(customerId)
                .customerEmail(customerEmail)
                .totalAmount(BigDecimal.ZERO) // will calculate
                .shippingFee(totalShipping)
                .taxAmount(BigDecimal.ZERO)
                .paymentMethod(request.getPaymentMethod() != null ? request.getPaymentMethod() : "CREDIT_CARD")
                .paymentStatus("PAID")
                .derivedStatus(DerivedOrderStatus.PLACED)
                .shippingAddress(request.getShippingAddress())
                .shippingCity(request.getShippingCity())
                .shippingPostalCode(request.getShippingPostalCode())
                .build();

        // 4. Split into Sub-Orders per Seller
        int sellerIndex = 1;
        for (Map.Entry<Long, List<OrderItemRequest>> entry : sellerItemsMap.entrySet()) {
            Long sellerId = entry.getKey();
            List<OrderItemRequest> items = entry.getValue();

            String subOrderNumber = "SUB-" + parentOrderNumber + "-S" + sellerId;
            BigDecimal subtotal = BigDecimal.ZERO;

            SubOrder subOrder = SubOrder.builder()
                    .subOrderNumber(subOrderNumber)
                    .sellerId(sellerId)
                    .subtotal(BigDecimal.ZERO)
                    .shippingFee(BigDecimal.valueOf(5.00))
                    .status(SubOrderStatus.PLACED)
                    .build();

            for (OrderItemRequest itemReq : items) {
                ProductDto prod = productMap.get(itemReq.getProductId());
                BigDecimal unitPrice = (prod != null && prod.getPrice() != null) ? prod.getPrice() : BigDecimal.valueOf(99.99);
                String prodName = (prod != null && prod.getName() != null) ? prod.getName() : "Product #" + itemReq.getProductId();
                String sku = (prod != null && prod.getSku() != null) ? prod.getSku() : "SKU-" + itemReq.getProductId();
                BigDecimal itemSubtotal = unitPrice.multiply(BigDecimal.valueOf(itemReq.getQuantity()));

                subtotal = subtotal.add(itemSubtotal);

                SubOrderItem item = SubOrderItem.builder()
                        .productId(itemReq.getProductId())
                        .productName(prodName)
                        .sku(sku)
                        .unitPrice(unitPrice)
                        .quantity(itemReq.getQuantity())
                        .subtotal(itemSubtotal)
                        .build();

                subOrder.addItem(item);
            }

            subOrder.setSubtotal(subtotal);
            totalAmount = totalAmount.add(subtotal);
            parentOrder.addSubOrder(subOrder);
            sellerIndex++;
        }

        parentOrder.setTotalAmount(totalAmount.add(totalShipping));
        ParentOrder saved = parentOrderRepository.save(parentOrder);
        log.info("Created parent order id={}, orderNumber={} with {} sub-orders", saved.getId(), saved.getOrderNumber(), saved.getSubOrders().size());

        return mapParentToDto(saved);
    }

    // --- Customer Read & Cancel ---
    @Transactional(readOnly = true)
    public Page<ParentOrderDto> getCustomerOrders(Long customerId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return parentOrderRepository.findByCustomerId(customerId, pageable).map(this::mapParentToDto);
    }

    @Transactional(readOnly = true)
    public ParentOrderDto getCustomerOrder(String orderNumber, Long customerId) {
        ParentOrder order = parentOrderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found: " + orderNumber));

        if (!order.getCustomerId().equals(customerId)) {
            throw new AccessDeniedException("Access denied: You do not own this order");
        }
        return mapParentToDto(order);
    }

    @Transactional
    public SubOrderDto cancelSubOrderAsCustomer(String subOrderNumber, Long customerId, CancelSubOrderRequest request) {
        SubOrder subOrder = subOrderRepository.findBySubOrderNumber(subOrderNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Sub-order not found: " + subOrderNumber));

        if (!subOrder.getParentOrder().getCustomerId().equals(customerId)) {
            throw new AccessDeniedException("Access denied: You do not own this order");
        }

        if (subOrder.getStatus() != SubOrderStatus.PLACED) {
            throw new BadRequestException("Sub-order cannot be cancelled once confirmed or processed by the seller");
        }

        subOrder.setStatus(SubOrderStatus.CANCELLED);
        subOrder.setCancellationReason(request != null ? request.getReason() : "Cancelled by customer prior to seller confirmation");
        SubOrder saved = subOrderRepository.save(subOrder);

        // Recalculate parent order status
        subOrder.getParentOrder().recalculateDerivedStatus();
        parentOrderRepository.save(subOrder.getParentOrder());

        log.info("Customer {} cancelled sub-order {}", customerId, subOrderNumber);
        return mapSubOrderToDto(saved);
    }

    // --- Seller Operations (BOLA Protected) ---
    @Transactional(readOnly = true)
    public Page<SubOrderDto> getSellerSubOrders(Long sellerId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return subOrderRepository.findBySellerId(sellerId, pageable).map(this::mapSubOrderToDto);
    }

    @Transactional(readOnly = true)
    public SubOrderDto getSubOrderForSeller(String subOrderNumber, Long sellerId) {
        SubOrder subOrder = subOrderRepository.findBySubOrderNumber(subOrderNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Sub-order not found: " + subOrderNumber));

        if (!subOrder.getSellerId().equals(sellerId)) {
            log.warn("BOLA violation: Seller {} tried to view sub-order {} owned by seller {}", sellerId, subOrderNumber, subOrder.getSellerId());
            throw new AccessDeniedException("Access denied: Sub-order does not belong to your store");
        }
        return mapSubOrderToDto(subOrder);
    }

    @Transactional
    public SubOrderDto confirmSubOrder(String subOrderNumber, Long sellerId) {
        SubOrder subOrder = getSubOrderWithOwnership(subOrderNumber, sellerId);
        if (subOrder.getStatus() != SubOrderStatus.PLACED) {
            throw new BadRequestException("Sub-order can only be confirmed from PLACED status. Current: " + subOrder.getStatus());
        }
        subOrder.setStatus(SubOrderStatus.CONFIRMED);
        SubOrder saved = subOrderRepository.save(subOrder);
        subOrder.getParentOrder().recalculateDerivedStatus();
        parentOrderRepository.save(subOrder.getParentOrder());
        log.info("Seller {} confirmed sub-order {}", sellerId, subOrderNumber);
        return mapSubOrderToDto(saved);
    }

    @Transactional
    public SubOrderDto packSubOrder(String subOrderNumber, Long sellerId) {
        SubOrder subOrder = getSubOrderWithOwnership(subOrderNumber, sellerId);
        if (subOrder.getStatus() != SubOrderStatus.CONFIRMED) {
            throw new BadRequestException("Sub-order must be confirmed before packing. Current: " + subOrder.getStatus());
        }
        subOrder.setStatus(SubOrderStatus.PACKED);
        SubOrder saved = subOrderRepository.save(subOrder);
        subOrder.getParentOrder().recalculateDerivedStatus();
        parentOrderRepository.save(subOrder.getParentOrder());
        log.info("Seller {} packed sub-order {}", sellerId, subOrderNumber);
        return mapSubOrderToDto(saved);
    }

    @Transactional
    public SubOrderDto shipSubOrder(String subOrderNumber, Long sellerId, FulfillSubOrderRequest request) {
        SubOrder subOrder = getSubOrderWithOwnership(subOrderNumber, sellerId);
        if (subOrder.getStatus() != SubOrderStatus.PACKED && subOrder.getStatus() != SubOrderStatus.CONFIRMED) {
            throw new BadRequestException("Sub-order must be confirmed/packed before shipping. Current: " + subOrder.getStatus());
        }
        subOrder.setStatus(SubOrderStatus.SHIPPED);
        subOrder.setCarrier(request.getCarrier().trim());
        subOrder.setTrackingCode(request.getTrackingCode().trim());
        SubOrder saved = subOrderRepository.save(subOrder);
        subOrder.getParentOrder().recalculateDerivedStatus();
        parentOrderRepository.save(subOrder.getParentOrder());
        log.info("Seller {} shipped sub-order {} with carrier {} / tracking {}", sellerId, subOrderNumber, request.getCarrier(), request.getTrackingCode());
        return mapSubOrderToDto(saved);
    }

    @Transactional
    public SubOrderDto cancelSubOrderAsSeller(String subOrderNumber, Long sellerId, CancelSubOrderRequest request) {
        SubOrder subOrder = getSubOrderWithOwnership(subOrderNumber, sellerId);
        if (subOrder.getStatus() == SubOrderStatus.SHIPPED || subOrder.getStatus() == SubOrderStatus.DELIVERED) {
            throw new BadRequestException("Cannot cancel an already shipped/delivered order");
        }
        subOrder.setStatus(SubOrderStatus.CANCELLED);
        subOrder.setCancellationReason(request != null ? request.getReason() : "Cancelled by seller (e.g. out of stock)");
        SubOrder saved = subOrderRepository.save(subOrder);
        subOrder.getParentOrder().recalculateDerivedStatus();
        parentOrderRepository.save(subOrder.getParentOrder());
        log.info("Seller {} cancelled sub-order {}", sellerId, subOrderNumber);
        return mapSubOrderToDto(saved);
    }

    // --- Admin Platform View (View-Only) ---
    @Transactional(readOnly = true)
    public Page<ParentOrderDto> getAllOrders(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return parentOrderRepository.findAll(pageable).map(this::mapParentToDto);
    }

    private SubOrder getSubOrderWithOwnership(String subOrderNumber, Long sellerId) {
        SubOrder subOrder = subOrderRepository.findBySubOrderNumber(subOrderNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Sub-order not found: " + subOrderNumber));
        if (!subOrder.getSellerId().equals(sellerId)) {
            log.warn("BOLA violation: Seller {} attempted modification of sub-order {} owned by seller {}", sellerId, subOrderNumber, subOrder.getSellerId());
            throw new AccessDeniedException("Access denied: Sub-order does not belong to your store");
        }
        return subOrder;
    }

    public ParentOrderDto mapParentToDto(ParentOrder order) {
        List<SubOrderDto> subOrderDtos = order.getSubOrders() != null
                ? order.getSubOrders().stream().map(this::mapSubOrderToDto).toList()
                : Collections.emptyList();

        return ParentOrderDto.builder()
                .id(order.getId())
                .orderNumber(order.getOrderNumber())
                .customerId(order.getCustomerId())
                .customerEmail(order.getCustomerEmail())
                .totalAmount(order.getTotalAmount())
                .shippingFee(order.getShippingFee())
                .taxAmount(order.getTaxAmount())
                .paymentMethod(order.getPaymentMethod())
                .paymentStatus(order.getPaymentStatus())
                .derivedStatus(order.getDerivedStatus())
                .shippingAddress(order.getShippingAddress())
                .shippingCity(order.getShippingCity())
                .shippingPostalCode(order.getShippingPostalCode())
                .subOrders(subOrderDtos)
                .createdAt(order.getCreatedAt())
                .build();
    }

    public SubOrderDto mapSubOrderToDto(SubOrder subOrder) {
        List<SubOrderItemDto> itemDtos = subOrder.getItems() != null
                ? subOrder.getItems().stream().map(i -> SubOrderItemDto.builder()
                        .id(i.getId())
                        .productId(i.getProductId())
                        .productName(i.getProductName())
                        .sku(i.getSku())
                        .unitPrice(i.getUnitPrice())
                        .quantity(i.getQuantity())
                        .subtotal(i.getSubtotal())
                        .build()).toList()
                : Collections.emptyList();

        return SubOrderDto.builder()
                .id(subOrder.getId())
                .parentOrderId(subOrder.getParentOrder() != null ? subOrder.getParentOrder().getId() : null)
                .orderNumber(subOrder.getParentOrder() != null ? subOrder.getParentOrder().getOrderNumber() : null)
                .subOrderNumber(subOrder.getSubOrderNumber())
                .sellerId(subOrder.getSellerId())
                .sellerStoreName(subOrder.getSellerId() == 1L ? "Apex Electronics Store" : (subOrder.getSellerId() == 2L ? "Nordic Home & Living" : "Partner Store #" + subOrder.getSellerId()))
                .subtotal(subOrder.getSubtotal())
                .shippingFee(subOrder.getShippingFee())
                .status(subOrder.getStatus())
                .cancellationReason(subOrder.getCancellationReason())
                .returnReason(subOrder.getReturnReason())
                .carrier(subOrder.getCarrier())
                .trackingCode(subOrder.getTrackingCode())
                .deliveredAt(subOrder.getDeliveredAt())
                .items(itemDtos)
                .createdAt(subOrder.getCreatedAt())
                .build();
    }
}
