package com.simba.customerservice.service;

import com.simba.ApiResponse;
import com.simba.customer.payload.CustomerData;
import com.simba.customer.command.CreateCustomerCommand;

public interface CustomerService {
    ApiResponse<CustomerData> create(CreateCustomerCommand request);
    ApiResponse<CustomerData> getById(String id);
}
