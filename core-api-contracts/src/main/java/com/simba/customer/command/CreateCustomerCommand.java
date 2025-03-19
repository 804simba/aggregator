package com.simba.customer.command;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class CreateCustomerCommand {
    @NotEmpty(message = "Please provide an id")
    @JsonProperty("id")
    private String id;

    @NotEmpty(message = "Please provide an email")
    @JsonProperty("email")
    private String email;

    @NotEmpty(message = "Please provide a full name")
    @JsonProperty("full_name")
    private String fullName;
}
