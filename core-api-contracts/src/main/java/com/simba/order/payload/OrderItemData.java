package com.simba.order.payload;

import lombok.Data;

@Data
public class OrderItemData {
    private String productId;
    private int quantity;
    private double price;
    private String imageUrl;
    private String productName;
}
