package com.simba.payment.events;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PaymentConfirmedEvent {
    private String orderId;
    private String paymentId;
    private BigDecimal amount;
    private String status;
}
