package com.simba.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class ErrorResponse {
    private boolean success;
    private int code;
    private String message;
    private List<String> errors;
    private LocalDateTime timestamp;
}
