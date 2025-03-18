package com.simba.order.events;

import lombok.Data;

@Data
public class OrderStatusUpdatedEvent {
    private String orderId;
    private String customerId;
    private String status;
    private final String updatedBy = "SYSTEM";
}
