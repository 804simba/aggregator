package com.simba.payment.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class PaymentRequest {
    @NotEmpty(message = "Please provide an id")
    @JsonProperty("id")
    private String id;

    @NotEmpty(message = "Please provide a customer id")
    @JsonProperty("customer_id")
    private String customerId;

    @NotEmpty(message = "Please provide a product id")
    @JsonProperty("product_id")
    private String productId;

    @NotEmpty(message = "Please provide a delivery address")
    @JsonProperty("delivery_address")
    private String deliveryAddress;
}
