package com.piehouse.woorepie.global.exception;

import com.piehouse.woorepie.global.response.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

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

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidationException(MethodArgumentNotValidException ex, HttpServletRequest request) {
        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getDefaultMessage())  // @NotBlank(message="...") 메시지 사용
                .collect(Collectors.toList());

        return ResponseEntity.badRequest().body(
                new ApiResponse<>(
                        LocalDateTime.now(),
                        400,
                        errors.get(0),
                        request.getRequestURI(),
                        null
                )
        );
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

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Void>> handleMissingBody(HttpMessageNotReadableException ex, HttpServletRequest request) {
        return ResponseEntity
                .badRequest()
                .body(new ApiResponse<>(
                        LocalDateTime.now(),
                        400,
                        "요청 body가 없습니다.",
                        request.getRequestURI(),
                        null
                ));
    }

}
