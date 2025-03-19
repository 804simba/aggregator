package com.simba;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public abstract class EventContract<T> {
    private String id;
    private T data;
    private String eventType;
    private String source;
    private LocalDateTime timestamp;
}
