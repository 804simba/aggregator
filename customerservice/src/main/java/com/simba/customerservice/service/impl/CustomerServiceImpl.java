package com.simba.customerservice.service.impl;

import com.simba.ApiResponse;
import com.simba.customer.payload.CustomerData;
import com.simba.customer.command.CreateCustomerCommand;
import com.simba.customerservice.model.Customer;
import com.simba.customerservice.repository.CustomerRepository;
import com.simba.customerservice.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {
    private final CustomerRepository customerRepository;
    @Override
    public ApiResponse<CustomerData> create(CreateCustomerCommand request) {
        var customer = Customer.builder()
                .email(request.getEmail())
                .fullName(request.getFullName())
                .build();
        customerRepository.save(customer);
        return ApiResponse.ok(CustomerData.builder()
                .id(customer.getId())
                .email(customer.getEmail())
                .fullName(customer.getFullName())
                .build());
    }

    @Override
    public ApiResponse<CustomerData> getById(String id) {
        var customer = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
        return ApiResponse.ok(CustomerData.builder()
                .id(customer.getId())
                .email(customer.getEmail())
                .fullName(customer.getFullName())
                .build());
    }
}
