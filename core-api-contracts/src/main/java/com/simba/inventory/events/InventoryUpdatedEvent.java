package com.simba.inventory.events;

import lombok.Data;

@Data
public class InventoryUpdatedEvent {
    private String productId;
    private int quantity;
    private String action;
}
