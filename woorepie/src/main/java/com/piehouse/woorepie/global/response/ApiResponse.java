package com.piehouse.woorepie.global.response;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiResponse<T>(
        LocalDateTime timestamp,
        int status,
        String message,
        String path,
        T data
) {
    public static <T> ApiResponse<T> of(int status, String message, String path, T data) {
        return new ApiResponse<>(LocalDateTime.now(), status, message, path, data);
    }
}
