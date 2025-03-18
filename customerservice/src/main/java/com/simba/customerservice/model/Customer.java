package com.simba.customerservice.model;

import lombok.*;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@EqualsAndHashCode(callSuper = true)
@Document(collection = "customers")
@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class Customer extends BaseEntity {

    @Indexed(name = "idx_customer_id", unique = true)
    private String email;

    @Field(name = "full_name")
    private String fullName;
}
