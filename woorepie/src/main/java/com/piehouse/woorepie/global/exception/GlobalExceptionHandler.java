package com.piehouse.woorepie.global.exception;

import com.piehouse.woorepie.global.response.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ApiResponse<Void>> handleCustomException(CustomException ex, HttpServletRequest request) {
        ErrorCode errorCode = ex.getErrorCode();

        return ResponseEntity.status(errorCode.getHttpStatus())
                .body(new ApiResponse<>(
                        LocalDateTime.now(),
                        errorCode.getHttpStatus().value(),
                        errorCode.getMessage(),
                        request.getRequestURI(),
                        null
                ));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleException(Exception ex, HttpServletRequest request) {
        ex.printStackTrace(); // 로그 출력
        return ResponseEntity.internalServerError().body(
                new ApiResponse<>(
                        LocalDateTime.now(),
                        500,
                        "알 수 없는 서버 오류",
                        request.getRequestURI(),
                        null
                )
        );
    }
}
