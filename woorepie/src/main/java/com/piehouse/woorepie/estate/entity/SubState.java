package com.piehouse.woorepie.estate.entity;

public enum SubState {
    READY, // 청약 등록 대기
    RUNNING, // 청약 중
    PENDING, // 청약 완료 (승인 대기)
    SUCCESS, // 청약 완료 (승인 완료)
    FAILURE // 청약 실패
}
