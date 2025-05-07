package com.piehouse.woorepie.global.exception;

import org.springframework.http.HttpStatus;

public enum ErrorCode {
    // 400: 잘못된 요청
    INVALID_REQUEST(HttpStatus.BAD_REQUEST, "잘못된 요청입니다."),
    VALIDATION_ERROR(HttpStatus.BAD_REQUEST, "입력값이 유효하지 않습니다."),
    TOKEN_NON_EXIST(HttpStatus.BAD_REQUEST, "해당 고객의 보유 내역이 없습니다."),
    INSUFFICIENT_CASH(HttpStatus.BAD_REQUEST, "보유 현금이 부족합니다."), // 추가

    // 401: 인증 실패
    INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED, "로그인 정보가 일치하지 않습니다."),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "인증이 필요합니다."),
    TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "토큰이 만료되었습니다."),

    // 403: 권한 없음
    FORBIDDEN(HttpStatus.FORBIDDEN, "접근 권한이 없습니다."),

    // 404: 리소스 없음
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 사용자를 찾을 수 없습니다."),
    ESTATE_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 매물을 찾을 수 없습니다."),
    RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND, "요청한 리소스를 찾을 수 없습니다."),
    ACCOUNT_NON_EXIST(HttpStatus.NOT_FOUND, "사용자 계좌를 찾을 수 없습니다."), // 추가

    // 409: 리소스 충돌
    DUPLICATE_RESOURCE(HttpStatus.CONFLICT, "이미 존재하는 리소스입니다."),

    // 500: 서버 에러
    INTERNAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 오류가 발생했습니다.");

    private final HttpStatus httpStatus;
    private final String message;

    ErrorCode(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public String getMessage() {
        return message;
    }
}
