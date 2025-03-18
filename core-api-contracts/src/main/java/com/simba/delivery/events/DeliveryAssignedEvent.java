package com.simba.delivery.events;

import lombok.Data;

@Data
public class DeliveryAssignedEvent {
    private String orderId;
    private String deliveryId;
    private String assignedTo;
    private String estimatedDeliveryTime;
}
