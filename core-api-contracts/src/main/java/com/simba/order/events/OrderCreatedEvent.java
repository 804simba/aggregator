package com.simba.order.events;

import com.simba.order.payload.OrderItemData;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderCreatedEvent {
    private String orderId;
    private String customerId;
    private String eventType;
    private String timestamp;
    private OrderItemData[] items;
    private BigDecimal totalAmount;
}
