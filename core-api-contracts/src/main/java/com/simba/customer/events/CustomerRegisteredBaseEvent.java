package com.simba.customer.events;

import lombok.Data;

@Data
public class CustomerRegisteredEvent {
    private String customerId;
    private String fullName;
    private String email;
}
