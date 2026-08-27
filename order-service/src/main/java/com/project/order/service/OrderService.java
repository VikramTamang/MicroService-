package com.project.order.service;

import com.project.order.client.ProductServiceClient;
import com.project.order.client.UserServiceClient;
import com.project.order.dto.*;
import com.project.order.dto.client.*;
import com.project.order.entity.Order;
import com.project.order.entity.OrderItem;
import com.project.order.entity.OrderStatus;
import com.project.order.exception.BadRequestException;
import com.project.order.exception.ResourceNotFoundException;
import com.project.order.exception.ServiceUnavailableException;
import com.project.order.repository.OrderRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Service
public class OrderService {

    private static final Logger log = LoggerFactory.getLogger(OrderService.class);

    private final OrderRepository orderRepository;
    private final ProductServiceClient productServiceClient;
    private final UserServiceClient userServiceClient;

    public OrderService(
            OrderRepository orderRepository,
            ProductServiceClient productServiceClient,
            UserServiceClient userServiceClient
    ) {
        this.orderRepository = orderRepository;
        this.productServiceClient = productServiceClient;
        this.userServiceClient = userServiceClient;
    }

    @Transactional
    public OrderDto createOrder(CreateOrderRequest request, Long authUserId, String authUserEmail) {
        log.info("Initiating order creation for user: {} (email: {})", authUserId, authUserEmail);

        Long effectiveUserId = authUserId != null ? authUserId : (request.getUserId() != null ? request.getUserId() : 1L);
        String effectiveEmail = authUserEmail != null ? authUserEmail : (request.getUserEmail() != null ? request.getUserEmail() : "guest@example.com");

        // 1. Validate user with User Service (guarded by CircuitBreaker)
        validateUser(effectiveUserId);

        // 2. Generate Tracking Number
        String trackingNumber = generateTrackingNumber();

        // 3. Build Stock Reservation Request
        List<StockReservationItem> reservationItems = request.getItems().stream()
                .map(item -> StockReservationItem.builder()
                        .productId(item.getProductId())
                        .quantity(item.getQuantity())
                        .build())
                .toList();

        StockReservationRequest stockRequest = StockReservationRequest.builder()
                .orderTrackingNumber(trackingNumber)
                .items(reservationItems)
                .build();

        // 4. Reserve stock with Product Service (guarded by CircuitBreaker)
        StockReservationResponse stockResponse = reserveStock(stockRequest);

        if (!stockResponse.isReserved() || stockResponse.getConfirmedItems() == null || stockResponse.getConfirmedItems().isEmpty()) {
            throw new BadRequestException("Failed to reserve product stock: " + stockResponse.getFailureReason());
        }

        // 5. Build and Save Order Entity
        Order order = Order.builder()
                .trackingNumber(trackingNumber)
                .userId(effectiveUserId)
                .userEmail(effectiveEmail)
                .totalAmount(stockResponse.getTotalAmount())
                .status(OrderStatus.CONFIRMED)
                .shippingAddress(request.getShippingAddress())
                .shippingCity(request.getShippingCity())
                .shippingPostalCode(request.getShippingPostalCode())
                .customerPhone(request.getCustomerPhone())
                .paymentMethod(request.getPaymentMethod() != null ? request.getPaymentMethod() : "CREDIT_CARD")
                .notes(request.getNotes())
                .build();

        for (StockReservationItem confirmed : stockResponse.getConfirmedItems()) {
            OrderItem orderItem = OrderItem.builder()
                    .productId(confirmed.getProductId())
                    .productName(confirmed.getProductName())
                    .unitPrice(confirmed.getUnitPrice())
                    .quantity(confirmed.getQuantity())
                    .subtotal(confirmed.getSubtotal())
                    .build();
            order.addItem(orderItem);
        }

        Order savedOrder = orderRepository.save(order);
        log.info("Order created successfully: id={}, tracking={}", savedOrder.getId(), savedOrder.getTrackingNumber());
        return mapToDto(savedOrder);
    }

    @CircuitBreaker(name = "productService", fallbackMethod = "reserveStockFallback")
    public StockReservationResponse reserveStock(StockReservationRequest request) {
        log.info("Invoking Product Service reserve-stock for {} items", request.getItems().size());
        ApiResponse<StockReservationResponse> response = productServiceClient.reserveStock(request);
        if (response != null && response.isSuccess() && response.getData() != null) {
            return response.getData();
        }
        throw new BadRequestException("Product service could not fulfill stock reservation: " + 
                (response != null ? response.getMessage() : "Unknown error"));
    }

    public StockReservationResponse reserveStockFallback(StockReservationRequest request, Throwable throwable) {
        log.error("CircuitBreaker fallback triggered for Product Service: {}", throwable.getMessage());
        throw new ServiceUnavailableException(
                "Product & Inventory Service is currently degraded or unreachable. Please try placing your order again in a moment. (CircuitBreaker Active)"
        );
    }

    @CircuitBreaker(name = "userService", fallbackMethod = "validateUserFallback")
    public void validateUser(Long userId) {
        log.info("Validating user with User Service: userId={}", userId);
        ApiResponse<UserDto> response = userServiceClient.getUserById(userId);
        if (response == null || !response.isSuccess() || response.getData() == null) {
            log.warn("User validation returned non-success for userId: {}", userId);
        }
    }

    public void validateUserFallback(Long userId, Throwable throwable) {
        log.warn("User Service unavailable for validation (CircuitBreaker fallback). Allowing order progression with caution. Reason: {}", throwable.getMessage());
        // Graceful degradation: allow checkout to continue if user service is temporarily down
    }

    @Transactional(readOnly = true)
    public List<OrderDto> getOrdersForUser(Long userId, String userEmail) {
        List<Order> orders;
        if (userId != null) {
            orders = orderRepository.findByUserIdOrderByCreatedAtDesc(userId);
        } else if (userEmail != null && !userEmail.isBlank()) {
            orders = orderRepository.findByUserEmailOrderByCreatedAtDesc(userEmail);
        } else {
            orders = List.of();
        }
        return orders.stream().map(this::mapToDto).toList();
    }

    @Transactional(readOnly = true)
    public OrderDto getOrderById(Long id) {
        return orderRepository.findById(id)
                .map(this::mapToDto)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));
    }

    @Transactional(readOnly = true)
    public OrderDto getOrderByTrackingNumber(String trackingNumber) {
        return orderRepository.findByTrackingNumber(trackingNumber)
                .map(this::mapToDto)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with tracking number: " + trackingNumber));
    }

    @Transactional(readOnly = true)
    public Page<OrderDto> getAllOrders(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return orderRepository.findAll(pageable).map(this::mapToDto);
    }

    @Transactional
    public OrderDto updateOrderStatus(Long id, OrderStatus status) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));

        order.setStatus(status);
        Order updated = orderRepository.save(order);
        log.info("Updated order id={} status to {}", id, status);
        return mapToDto(updated);
    }

    private String generateTrackingNumber() {
        String datePart = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String randomPart = UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        return "ORD-" + datePart + "-" + randomPart;
    }

    public OrderDto mapToDto(Order order) {
        List<OrderItemDto> itemDtos = order.getItems().stream()
                .map(item -> OrderItemDto.builder()
                        .id(item.getId())
                        .productId(item.getProductId())
                        .productName(item.getProductName())
                        .unitPrice(item.getUnitPrice())
                        .quantity(item.getQuantity())
                        .subtotal(item.getSubtotal())
                        .build())
                .toList();

        return OrderDto.builder()
                .id(order.getId())
                .trackingNumber(order.getTrackingNumber())
                .userId(order.getUserId())
                .userEmail(order.getUserEmail())
                .totalAmount(order.getTotalAmount())
                .status(order.getStatus())
                .shippingAddress(order.getShippingAddress())
                .shippingCity(order.getShippingCity())
                .shippingPostalCode(order.getShippingPostalCode())
                .customerPhone(order.getCustomerPhone())
                .paymentMethod(order.getPaymentMethod())
                .notes(order.getNotes())
                .items(itemDtos)
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .build();
    }
}
