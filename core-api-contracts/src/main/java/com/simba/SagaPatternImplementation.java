//package com.simba;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.UUID;
//import java.util.concurrent.CompletableFuture;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//// Core transaction status
//enum TransactionStatus {
//    STARTED, COMPLETED, FAILED, REVERTED
//}
//
//// Event model for saga coordination
//class SagaEvent {
//    private String eventId;
//    private String sagaId;
//    private String type;
//    private Object payload;
//    private long timestamp;
//
//    public SagaEvent(String sagaId, String type, Object payload) {
//        this.eventId = UUID.randomUUID().toString();
//        this.sagaId = sagaId;
//        this.type = type;
//        this.payload = payload;
//        this.timestamp = System.currentTimeMillis();
//    }
//
//    // Getters and setters omitted for brevity
//    public String getEventId() { return eventId; }
//    public String getSagaId() { return sagaId; }
//    public String getType() { return type; }
//    public Object getPayload() { return payload; }
//    public long getTimestamp() { return timestamp; }
//}
//
//// Step definition for saga
//interface SagaStep<T, R> {
//    CompletableFuture<R> execute(T data);
//    CompletableFuture<Void> compensate(T data);
//}
//
//// Orchestration-based saga implementation
//class OrderSaga {
//    private static final Logger logger = LoggerFactory.getLogger(OrderSaga.class);
//    private final String sagaId;
//    private final List<SagaEvent> events = new ArrayList<>();
//    private TransactionStatus status = TransactionStatus.STARTED;
//    private final EventPublisher eventPublisher;
//
//    public OrderSaga(EventPublisher eventPublisher) {
//        this.sagaId = UUID.randomUUID().toString();
//        this.eventPublisher = eventPublisher;
//    }
//
//    public String getSagaId() {
//        return sagaId;
//    }
//
//    public TransactionStatus getStatus() {
//        return status;
//    }
//
//    // Record and publish events
//    private void recordEvent(String type, Object payload) {
//        SagaEvent event = new SagaEvent(sagaId, type, payload);
//        events.add(event);
//        eventPublisher.publish(event);
//    }
//}
//
//// Order service implementation with saga pattern
//class OrderService {
//    private static final Logger logger = LoggerFactory.getLogger(OrderService.class);
//    private final EventPublisher eventPublisher;
//    private final OrderRepository orderRepository;
//    private final CustomerService customerService;
//    private final InventoryService inventoryService;
//    private final PaymentService paymentService;
//    private final DeliveryService deliveryService;
//    private final NotificationService notificationService;
//
//    public OrderService(
//            EventPublisher eventPublisher,
//            OrderRepository orderRepository,
//            CustomerService customerService,
//            InventoryService inventoryService,
//            PaymentService paymentService,
//            DeliveryService deliveryService,
//            NotificationService notificationService) {
//        this.eventPublisher = eventPublisher;
//        this.orderRepository = orderRepository;
//        this.customerService = customerService;
//        this.inventoryService = inventoryService;
//        this.paymentService = paymentService;
//        this.deliveryService = deliveryService;
//        this.notificationService = notificationService;
//    }
//
//    public CompletableFuture<OrderResult> processOrder(Order order) {
//        OrderSaga saga = new OrderSaga(eventPublisher);
//        String sagaId = saga.getSagaId();
//
//        // Associate order with saga
//        order.setSagaId(sagaId);
//        logger.info("Starting order processing saga: {}", sagaId);
//
//        return createOrder(order)
//                .thenCompose(createdOrder -> verifyCustomer(createdOrder))
//                .thenCompose(verifiedOrder -> reserveInventory(verifiedOrder))
//                .thenCompose(readyOrder -> processPayment(readyOrder))
//                .thenCompose(paidOrder -> scheduleDelivery(paidOrder))
//                .thenCompose(scheduledOrder -> sendConfirmation(scheduledOrder))
//                .thenApply(finalOrder -> {
//                    logger.info("Order saga completed successfully: {}", sagaId);
//                    return new OrderResult(finalOrder, true, "Order processed successfully");
//                })
//                .exceptionally(ex -> {
//                    logger.error("Order saga failed: {}", sagaId, ex);
//                    compensateOrder(order, ex);
//                    return new OrderResult(order, false, "Order processing failed: " + ex.getMessage());
//                });
//    }
//
//    private CompletableFuture<Order> createOrder(Order order) {
//        return CompletableFuture.supplyAsync(() -> {
//            logger.info("Creating order: {}", order.getId());
//            Order savedOrder = orderRepository.save(order);
//            eventPublisher.publish(new SagaEvent(order.getSagaId(), "ORDER_CREATED", savedOrder));
//            return savedOrder;
//        });
//    }
//
//    private CompletableFuture<Order> verifyCustomer(Order order) {
//        return CompletableFuture.supplyAsync(() -> {
//            logger.info("Verifying customer for order: {}", order.getId());
//            try {
//                CustomerVerification verification = customerService.verifyCustomer(order.getCustomerId());
//                if (!verification.isValid()) {
//                    throw new IllegalStateException("Customer verification failed: " + verification.getReason());
//                }
//                eventPublisher.publish(new SagaEvent(order.getSagaId(), "CUSTOMER_VERIFIED", verification));
//                return order;
//            } catch (Exception ex) {
//                logger.error("Customer verification failed for order: {}", order.getId(), ex);
//                throw new CompensatableException("Customer verification failed", ex, () -> {
//                    // No compensation needed - order will be cancelled
//                });
//            }
//        });
//    }
//
//    private CompletableFuture<Order> reserveInventory(Order order) {
//        return CompletableFuture.supplyAsync(() -> {
//            logger.info("Reserving inventory for order: {}", order.getId());
//            try {
//                InventoryReservation reservation = inventoryService.reserveItems(
//                        order.getId(), order.getItems());
//                if (!reservation.isSuccessful()) {
//                    throw new IllegalStateException("Inventory reservation failed: " + reservation.getReason());
//                }
//                order.setReservationId(reservation.getReservationId());
//                eventPublisher.publish(new SagaEvent(order.getSagaId(), "INVENTORY_RESERVED", reservation));
//                return order;
//            } catch (Exception ex) {
//                logger.error("Inventory reservation failed for order: {}", order.getId(), ex);
//                throw new CompensatableException("Inventory reservation failed", ex, () -> {
//                    // Compensation action not needed - nothing was reserved
//                });
//            }
//        });
//    }
//
//    private CompletableFuture<Order> processPayment(Order order) {
//        return CompletableFuture.supplyAsync(() -> {
//            logger.info("Processing payment for order: {}", order.getId());
//            try {
//                PaymentResult payment = paymentService.processPayment(
//                        order.getCustomerId(), order.getId(), order.getTotalAmount());
//                if (!payment.isSuccessful()) {
//                    throw new IllegalStateException("Payment failed: " + payment.getReason());
//                }
//                order.setPaymentId(payment.getPaymentId());
//                eventPublisher.publish(new SagaEvent(order.getSagaId(), "PAYMENT_PROCESSED", payment));
//                return order;
//            } catch (Exception ex) {
//                logger.error("Payment processing failed for order: {}", order.getId(), ex);
//                throw new CompensatableException("Payment processing failed", ex, () -> {
//                    // Compensation action: release inventory
//                    if (order.getReservationId() != null) {
//                        logger.info("Compensating - releasing inventory for order: {}", order.getId());
//                        inventoryService.releaseReservation(order.getReservationId());
//                        eventPublisher.publish(new SagaEvent(order.getSagaId(), "INVENTORY_RELEASED",
//                                order.getReservationId()));
//                    }
//                });
//            }
//        });
//    }
//
//    private CompletableFuture<Order> scheduleDelivery(Order order) {
//        return CompletableFuture.supplyAsync(() -> {
//            logger.info("Scheduling delivery for order: {}", order.getId());
//            try {
//                DeliverySchedule schedule = deliveryService.scheduleDelivery(
//                        order.getId(), order.getCustomerId(), order.getDeliveryAddress());
//                if (!schedule.isSuccessful()) {
//                    throw new IllegalStateException("Delivery scheduling failed: " + schedule.getReason());
//                }
//                order.setDeliveryId(schedule.getDeliveryId());
//                eventPublisher.publish(new SagaEvent(order.getSagaId(), "DELIVERY_SCHEDULED", schedule));
//                return order;
//            } catch (Exception ex) {
//                logger.error("Delivery scheduling failed for order: {}", order.getId(), ex);
//                throw new CompensatableException("Delivery scheduling failed", ex, () -> {
//                    // Compensation actions: refund payment and release inventory
//                    if (order.getPaymentId() != null) {
//                        logger.info("Compensating - refunding payment for order: {}", order.getId());
//                        paymentService.refundPayment(order.getPaymentId());
//                        eventPublisher.publish(new SagaEvent(order.getSagaId(), "PAYMENT_REFUNDED",
//                                order.getPaymentId()));
//                    }
//
//                    if (order.getReservationId() != null) {
//                        logger.info("Compensating - releasing inventory for order: {}", order.getId());
//                        inventoryService.releaseReservation(order.getReservationId());
//                        eventPublisher.publish(new SagaEvent(order.getSagaId(), "INVENTORY_RELEASED",
//                                order.getReservationId()));
//                    }
//                });
//            }
//        });
//    }
//
//    private CompletableFuture<Order> sendConfirmation(Order order) {
//        return CompletableFuture.supplyAsync(() -> {
//            logger.info("Sending confirmation for order: {}", order.getId());
//            try {
//                NotificationResult notification = notificationService.sendOrderConfirmation(
//                        order.getCustomerId(), order);
//                if (!notification.isSuccessful()) {
//                    logger.warn("Notification failed but proceeding with order: {}", notification.getReason());
//                }
//                order.setStatus("CONFIRMED");
//                Order updatedOrder = orderRepository.update(order);
//                eventPublisher.publish(new SagaEvent(order.getSagaId(), "ORDER_CONFIRMED", updatedOrder));
//                return updatedOrder;
//            } catch (Exception ex) {
//                // Non-critical step - log but don't fail the transaction
//                logger.error("Confirmation failed for order: {}", order.getId(), ex);
//                order.setStatus("PROCESSING");
//                Order updatedOrder = orderRepository.update(order);
//                return updatedOrder;
//            }
//        });
//    }
//
//    private void compensateOrder(Order order, Throwable ex) {
//        logger.info("Starting compensation for failed order: {}", order.getId());
//
//        if (ex instanceof CompensatableException) {
//            ((CompensatableException) ex).compensate();
//        }
//
//        // Update order status to FAILED
//        try {
//            order.setStatus("FAILED");
//            order.setFailureReason(ex.getMessage());
//            orderRepository.update(order);
//            eventPublisher.publish(new SagaEvent(order.getSagaId(), "ORDER_FAILED", order));
//        } catch (Exception updateEx) {
//            logger.error("Failed to update order status: {}", order.getId(), updateEx);
//        }
//
//        // Send failure notification
//        try {
//            notificationService.sendOrderFailureNotification(order.getCustomerId(), order, ex.getMessage());
//        } catch (Exception notifyEx) {
//            logger.error("Failed to send failure notification: {}", order.getId(), notifyEx);
//        }
//    }
//}
//
//// Custom exception for compensation logic
//class CompensatableException extends RuntimeException {
//    private final Runnable compensationAction;
//
//    public CompensatableException(String message, Throwable cause, Runnable compensationAction) {
//        super(message, cause);
//        this.compensationAction = compensationAction;
//    }
//
//    public void compensate() {
//        compensationAction.run();
//    }
//}
//
//// Event publisher interface
//interface EventPublisher {
//    void publish(SagaEvent event);
//}
//
//// Kafka-based event publisher implementation
//class KafkaEventPublisher implements EventPublisher {
//    private static final Logger logger = LoggerFactory.getLogger(KafkaEventPublisher.class);
//    private final KafkaTemplate<String, SagaEvent> kafkaTemplate;
//    private final String topic;
//
//    public KafkaEventPublisher(KafkaTemplate<String, SagaEvent> kafkaTemplate, String topic) {
//        this.kafkaTemplate = kafkaTemplate;
//        this.topic = topic;
//    }
//
//    @Override
//    public void publish(SagaEvent event) {
//        logger.info("Publishing event: {} for saga: {}", event.getType(), event.getSagaId());
//        kafkaTemplate.send(topic, event.getSagaId(), event);
//    }
//}
//
//// Domain models and service interfaces (simplified)
//class Order {
//    private String id;
//    private String customerId;
//    private List<OrderItem> items;
//    private double totalAmount;
//    private String status;
//    private String deliveryAddress;
//    private String sagaId;
//    private String reservationId;
//    private String paymentId;
//    private String deliveryId;
//    private String failureReason;
//
//    // Getters and setters omitted for brevity
//    public String getId() { return id; }
//    public void setId(String id) { this.id = id; }
//    public String getCustomerId() { return customerId; }
//    public void setCustomerId(String customerId) { this.customerId = customerId; }
//    public List<OrderItem> getItems() { return items; }
//    public void setItems(List<OrderItem> items) { this.items = items; }
//    public double getTotalAmount() { return totalAmount; }
//    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }
//    public String getStatus() { return status; }
//    public void setStatus(String status) { this.status = status; }
//    public String getDeliveryAddress() { return deliveryAddress; }
//    public void setDeliveryAddress(String deliveryAddress) { this.deliveryAddress = deliveryAddress; }
//    public String getSagaId() { return sagaId; }
//    public void setSagaId(String sagaId) { this.sagaId = sagaId; }
//    public String getReservationId() { return reservationId; }
//    public void setReservationId(String reservationId) { this.reservationId = reservationId; }
//    public String getPaymentId() { return paymentId; }
//    public void setPaymentId(String paymentId) { this.paymentId = paymentId; }
//    public String getDeliveryId() { return deliveryId; }
//    public void setDeliveryId(String deliveryId) { this.deliveryId = deliveryId; }
//    public String getFailureReason() { return failureReason; }
//    public void setFailureReason(String failureReason) { this.failureReason = failureReason; }
//}
//
//class OrderItem {
//    private String itemId;
//    private int quantity;
//    private double price;
//
//    // Getters and setters omitted for brevity
//}
//
//class OrderResult {
//    private final Order order;
//    private final boolean success;
//    private final String message;
//
//    public OrderResult(Order order, boolean success, String message) {
//        this.order = order;
//        this.success = success;
//        this.message = message;
//    }
//
//    // Getters omitted for brevity
//    public Order getOrder() { return order; }
//    public boolean isSuccess() { return success; }
//    public String getMessage() { return message; }
//}
//
//// Service interfaces
//interface OrderRepository {
//    Order save(Order order);
//    Order update(Order order);
//    Order findById(String id);
//}
//
//interface CustomerService {
//    CustomerVerification verifyCustomer(String customerId);
//}
//
//class CustomerVerification {
//    private boolean valid;
//    private String reason;
//
//    // Getters and constructors omitted for brevity
//    public boolean isValid() { return valid; }
//}
//
//interface InventoryService {
//    InventoryReservation reserveItems(String orderId, List<OrderItem> items);
//    void releaseReservation(String reservationId);
//}
//
//class InventoryReservation {
//    private boolean successful;
//    private String reservationId;
//    private String reason;
//
//    // Getters and constructors omitted for brevity
//    public boolean isSuccessful() { return successful; }
//    public String getReservationId() { return reservationId; }
//    public String getReason() { return reason; }
//}
//
//interface PaymentService {
//    PaymentResult processPayment(String customerId, String orderId, double amount);
//    void refundPayment(String paymentId);
//}
//
//class PaymentResult {
//    private boolean successful;
//    private String paymentId;
//    private String reason;
//
//    // Getters and constructors omitted for brevity
//    public boolean isSuccessful() { return successful; }
//    public String getPaymentId() { return paymentId; }
//    public String getReason() { return reason; }
//}
//
//interface DeliveryService {
//    DeliverySchedule scheduleDelivery(String orderId, String customerId, String address);
//    void cancelDelivery(String deliveryId);
//}
//
//class DeliverySchedule {
//    private boolean successful;
//    private String deliveryId;
//    private String reason;
//
//    // Getters and constructors omitted for brevity
//    public boolean isSuccessful() { return successful; }
//    public String getDeliveryId() { return deliveryId; }
//    public String getReason() { return reason; }
//}
//
//interface NotificationService {
//    NotificationResult sendOrderConfirmation(String customerId, Order order);
//    NotificationResult sendOrderFailureNotification(String customerId, Order order, String reason);
//}
//
//class NotificationResult {
//    private boolean successful;
//    private String reason;
//
//    // Getters and constructors omitted for brevity
//    public boolean isSuccessful() { return successful; }
//    public String getReason() { return reason; }
//}
