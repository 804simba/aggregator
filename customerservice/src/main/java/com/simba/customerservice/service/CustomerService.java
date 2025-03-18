package com.simba.customerservice.service;

import com.simba.ApiResponse;
import com.simba.customer.payload.CustomerData;
import com.simba.customer.request.CreateCustomerRequest;

public interface CustomerService {
    ApiResponse<CustomerData> create(CreateCustomerRequest request);
    ApiResponse<CustomerData> getById(String id);
}
