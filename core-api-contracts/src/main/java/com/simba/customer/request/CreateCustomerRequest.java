package com.simba.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class CreateCustomerRequest {
    @NotEmpty(message = "Please provide an id")
    @JsonProperty("id")
    private String id;
    private String email;
    private String fullName;
}
