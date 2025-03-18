package com.simba.customerservice.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
public abstract class BaseEntity {
    @Id
    private String id;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
