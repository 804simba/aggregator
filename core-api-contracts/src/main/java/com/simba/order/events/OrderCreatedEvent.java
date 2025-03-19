package com.simba.order.events;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.simba.BaseEvent;
import com.simba.order.payload.OrderItemData;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class OrderCreatedEvent extends BaseEvent {
    private String orderId;
    private String customerId;
    private String eventType;
    private OrderItemData[] items;
    private BigDecimal totalAmount;
}
