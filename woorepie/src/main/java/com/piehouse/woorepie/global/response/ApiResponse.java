package com.piehouse.woorepie.global.response;

import java.time.LocalDateTime;

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
