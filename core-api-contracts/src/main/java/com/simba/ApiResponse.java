package com.simba;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class ApiResponse<T> {
    private boolean success;
    private int code;
    private String message;
    private List<String> errors;
    private T data;

    public static <T> ApiResponse<T> ok(T data) {
        return ApiResponse.<T>builder()
                .success(true)
                .code(200)
                .message("success")
                .build();
    }

    public static ApiResponse<?> badRequest(List<String> errorMessages) {
        return ApiResponse.builder()
                .success(false)
                .code(400)
                .message("failed")
                .errors(errorMessages)
                .build();
    }

    public static ApiResponse<?> notFound(List<String> errorMessages) {
        return ApiResponse.builder()
                .success(false)
                .code(404)
                .message("not found")
                .errors(errorMessages)
                .build();
    }
}
